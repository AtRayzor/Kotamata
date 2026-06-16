package dev.timray.kotomata.builder

import dev.timray.kotomata.builder.vertices.AtomicStateDeclaration
import dev.timray.kotomata.model.CompletionEvent
import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.Region
import dev.timray.kotomata.model.RegionAtom
import dev.timray.kotomata.model.TransitionAction
import dev.timray.kotomata.model.TransitionGuard
import dev.timray.kotomata.model.TriggeredTransitionAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass


@StateMachineDsl
interface StateBuilderScope<S : Any, C> {
    fun entry(action: (C) -> C)
    fun work(action: suspend CoroutineScope.(C) -> Deferred<C>)
    fun work(
        context: CoroutineContext,
        action: suspend CoroutineScope.(C) -> Deferred<C>
    )

    fun exit(action: (C) -> C)

    fun <E : Event> on(eventClass: KClass<E>): TransitionBuilder<S, E>

    fun onCompletion(): CompletionTransitionBuilder<S>

    infix fun <T : Any, E : Event> TransitionBuilder<S, E>.target(targetId: T): TargetedTransitionBuilder<S, T, E>

    fun <E : Event> TransitionBuilder<S, E>.targetSelf(): SelfTransitionBuilder<S, E>

    infix fun <T : Any, E : Event> TargetedTransitionBuilder<S, T, E>.withGuard(
        guard: TransitionGuard<C>
    ): TargetedTransitionBuilder<S, T, E>

    infix fun <T : Any, E : Event> TargetedTransitionBuilder<S, T, E>.withAction(
        action: TriggeredTransitionAction<C, E>
    ): TargetedTransitionBuilder<S, T, E>

    infix fun <E : Event> SelfTransitionBuilder<S, E>.withGuard(
        guard: TransitionGuard<C>
    ): SelfTransitionBuilder<S, E>

    infix fun <E : Event> SelfTransitionBuilder<S, E>.withAction(
        action: TriggeredTransitionAction<C, E>
    ): SelfTransitionBuilder<S, E>

    fun <T : Any> CompletionTransitionBuilder<S>.target(targetId: T): TargetedCompletionTransitionBuilder<S, T>

    fun <T : Any> TargetedCompletionTransitionBuilder<S, T>.withAction(action: TransitionAction<C>): TargetedCompletionTransitionBuilder<S, T>
}


interface CompositeStateBuilderScope<S : Any, C> :
    StateBuilderScope<S, C>,
    RegionOrCompositeStateBuilderScope<S, C>

interface SimpleStateBuilderScope<S : Any, C> : StateBuilderScope<S, C>


internal class StateBuilderScopeImpl<S : Any, C>(
    id: S,
    containingStateId: Any,
    region: Region
) :
    StateBuilderScope<S, C> {
    private var _declaration =
        AtomicStateDeclaration<S, C>(
            id = id,
            containingStateId = containingStateId,
            region = region
        )

    private val _transitionDeclarationSources =
        mutableMapOf<KClass<Event>, TransitionDeclarationSource>()

    val declaration: AtomicStateDeclaration<S, C>
        get() {
            update { dec -> dec.copy(transitions = _transitionDeclarationSources.values.map { src -> src.declaration }) }
            return _declaration
        }

    override fun entry(action: (C) -> C) {
        update { dec -> dec.copy(entryAction = action) }
    }

    override fun work(action: suspend CoroutineScope.(C) -> Deferred<C>) {
        update { dec -> dec.copy(doAction = action) }
    }

    override fun work(
        context: CoroutineContext,
        action: suspend CoroutineScope.(C) -> Deferred<C>
    ) {
        update { dec ->
            dec.copy(
                doAction = action,
                doContext = context
            )
        }
    }

    override fun exit(action: (C) -> C) {
        update { dec -> dec.copy(exitAction = action) }
    }

    override fun <E : Event> on(eventClass: KClass<E>): TransitionBuilder<S, E> =
        TransitionBuilderImpl(
            source = _declaration.id,
            eventClass = eventClass
        )

    override fun onCompletion(): CompletionTransitionBuilder<S> =
        CompletionTransitionBuilderImpl(declaration.id)

    override fun <T : Any, E : Event> TransitionBuilder<S, E>.target(
        targetId: T
    ): TargetedTransitionBuilder<S, T, E> =
        with(this as TransitionBuilderImpl<S, E>) {
            require(!_transitionDeclarationSources.contains(eventClass as KClass<Event>))
            val ec = eventClass as KClass<E>

            TargetedTransitionBuilderImpl(
                source = source,
                eventClass = ec,
                target = targetId
            ).also { builder ->
                _transitionDeclarationSources[eventClass as KClass<Event>] =
                    builder
            }
        }

    override fun <E : Event> TransitionBuilder<S, E>.targetSelf(): SelfTransitionBuilder<S, E> =
        with(this as TransitionBuilderImpl<S, E>) {
            require(!_transitionDeclarationSources.contains(eventClass as KClass<Event>))
            val ec = eventClass as KClass<E>

            SelfTransitionBuilderImpl(
                source = source,
                eventClass = ec
            ).also { builder ->
                _transitionDeclarationSources[eventClass as KClass<Event>] =
                    builder
            }
        }

    override fun <T : Any, E : Event> TargetedTransitionBuilder<S, T, E>.withGuard(
        guard: TransitionGuard<C>
    ): TargetedTransitionBuilder<S, T, E> =
        with(this as TargetedTransitionBuilderImpl<S, T, E>) {
            update { dec -> dec.copy(guard = { c -> guard(c as C) }) }
        }

    override fun <T : Any, E : Event> TargetedTransitionBuilder<S, T, E>.withAction(
        action: TriggeredTransitionAction<C, E>
    ): TargetedTransitionBuilder<S, T, E> =
        with(this as TargetedTransitionBuilderImpl<S, T, E>) {
            update { dec ->
                dec.copy(action = { c, e ->
                    action(
                        c as C,
                        e as E
                    )
                })
            }
        }

    override fun <E : Event> SelfTransitionBuilder<S, E>.withGuard(
        guard: TransitionGuard<C>
    ): SelfTransitionBuilder<S, E> =
        with(this as SelfTransitionBuilderImpl<S, E>) {
            update { dec -> dec.copy(guard = { c -> guard(c as C) }) }
        }

    override fun <E : Event> SelfTransitionBuilder<S, E>.withAction(
        action: TriggeredTransitionAction<C, E>
    ): SelfTransitionBuilder<S, E> =
        with(this as SelfTransitionBuilderImpl<S, E>) {
            update { dec ->
                dec.copy(action = { c, e ->
                    action(
                        c as C,
                        e as E
                    )
                })
            }
        }

    override fun <T : Any> CompletionTransitionBuilder<S>.target(targetId: T): TargetedCompletionTransitionBuilder<S, T> =
        with(this as CompletionTransitionBuilderImpl) {
            TargetedCompletionTransitionBuilderImpl(
                source = source,
                target = targetId
            ).also { builder ->
                _transitionDeclarationSources[CompletionEvent::class as KClass<Event>] =
                    builder
            }
        }


    override fun <T : Any> TargetedCompletionTransitionBuilder<S, T>.withAction(
        action: TransitionAction<C>
    ): TargetedCompletionTransitionBuilder<S, T> =
        with(this as TargetedCompletionTransitionBuilderImpl<S, T>) {
            update { dec -> dec.copy(action = { c -> action(c as C) }) }
        }

    private fun update(
        updater: (AtomicStateDeclaration<S, C>) -> AtomicStateDeclaration<S, C>
    ) {
        _declaration = updater(_declaration)
    }
}

internal class SimpleStateBuilderScopeImpl<S : Any, C>(
    id: S,
    containingStateId: Any,
    region: Region,
    private val impl: StateBuilderScopeImpl<S, C> = StateBuilderScopeImpl(
        id,
        containingStateId,
        region
    )
) :
    SimpleStateBuilderScope<S, C>,
    StateBuilderScope<S, C> by impl

internal class CompositeStateBuilderScopeImpl<S : Any, C>(
    id: S,
    containingStateId: Any,
    region: Region,
     val stateBuilderImpl: StateBuilderScopeImpl<S, C> = StateBuilderScopeImpl(
        id,
        containingStateId,
        region
    ),
    val regionOrCompositeStateBuilderImpl: RegionOrCompositeStateBuilderScopeImpl<S, C> =
        RegionOrCompositeStateBuilderScopeImpl(containingStateId, region)
) : CompositeStateBuilderScope<S, C>,
    StateBuilderScope<S, C> by stateBuilderImpl,
    RegionOrCompositeStateBuilderScope<S, C> by regionOrCompositeStateBuilderImpl