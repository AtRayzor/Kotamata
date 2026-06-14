package dev.timray.kotomata.builder

import dev.timray.kotomata.model.CompositeState
import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.RegionNode
import dev.timray.kotomata.model.AtomicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass


@StateMachineDsl
interface StateBuilderScope<S: Any, C> {
    fun  entry(action: (C) -> C)
    fun work(action: suspend CoroutineScope.(C) -> Deferred<C>)
    fun  work(context: CoroutineContext, action: suspend CoroutineScope.(C) -> Deferred<C>)
    fun  exit(action: (C) -> C)
    fun <E: Event> on(eventType: KClass<E>): TransitionBuilder<S, C, E>
}

inline fun <S, C, reified E> StateBuilderScope<S, C>.on()
    where S: Any,
          E: Event = on(E::class)

interface CompositeStateBuilderScope<S: Any, C>:
    StateBuilderScope<S, C>,
    StateMachineOrCompositeStateBuilderScope<S, C>

interface SimpleStateBuilderScope<S: Any, C>: StateBuilderScope<S, C>


internal class StateBuilderScopeImpl<S: Any, C>(val id: S): StateBuilderScope<S, C>{
    var entryAction: ((C) -> C)? = null
    var workAction: (suspend CoroutineScope.(C) -> Deferred<C>)? = null
    var workContext: CoroutineContext? = null
    var exitAction: ((C) -> C)? = null
    val transitionNodes = mutableListOf<TransitionBuilder<*,*,*>>()

    override fun entry(action: (C) -> C) {
        entryAction = action
    }

    override fun work(action: suspend CoroutineScope.(C) -> Deferred<C>) {
        workAction = action
    }

    override fun work(
        context: CoroutineContext,
        action: suspend CoroutineScope.(C) -> Deferred<C>
    ) {
        workContext = context
        workAction = action
    }

    override fun exit(action: (C) -> C) {
        exitAction = action
    }

    override fun <E : Event> on(eventType: KClass<E>): TransitionBuilder<S, C, E> {
        TODO("Not yet implemented")
    }
}

internal class SimpleStateBuilderScopeImpl<S: Any, C>(
    id: S,
    private val impl: StateBuilderScopeImpl<S, C> = StateBuilderScopeImpl(id)
):
    SimpleStateBuilderScope<S, C>,
    StateBuilderScope<S, C> by impl {

    fun build(): AtomicState<S, C> = with(impl) {
        return AtomicState(
            id = id,
            entryAction = entryAction,
            exitAction = exitAction,
            doAction = workAction
        )
    }
}

internal class CompositeStateBuilderScopeImpl<S: Any, C>(
    private val id: S,
    private val regionNode: RegionNode,
    private val stateBuilderImpl: StateBuilderScopeImpl<S, C> = StateBuilderScopeImpl(id),
    private val compositesBuilderImpl: StateMachineBuilderOrCompositeStateBuilderScopeImpl<S, C> =
        StateMachineBuilderOrCompositeStateBuilderScopeImpl(regionNode)
): CompositeStateBuilderScope<S, C>,
    StateBuilderScope<S, C> by stateBuilderImpl,
        StateMachineOrCompositeStateBuilderScope<S, C> by compositesBuilderImpl {

    fun build(): CompositeState<S, C> {
        TODO()
    }
}