package dev.timray.kotomata.builder

import dev.timray.kotomata.builder.vertices.CompletionTransitionDeclaration
import dev.timray.kotomata.builder.vertices.SelfTransitionDeclaration
import dev.timray.kotomata.builder.vertices.TransitionDeclaration
import dev.timray.kotomata.builder.vertices.TriggeredTransitionDeclaration
import dev.timray.kotomata.model.Event
import kotlin.reflect.KClass

sealed interface TransitionBuilder<S : Any, E : Event>

sealed interface TargetedTransitionBuilder<S : Any, T : Any, E : Event> :
    TransitionBuilder<S, E>


sealed interface SelfTransitionBuilder<S : Any, E : Event> :
    TransitionBuilder<S, E>

sealed interface CompletionTransitionBuilder<S : Any>
sealed interface TargetedCompletionTransitionBuilder<S : Any, T : Any> :
    CompletionTransitionBuilder<S>


internal sealed interface TransitionDeclarationSource {
    val declaration: TransitionDeclaration
}

internal class TransitionBuilderImpl<S : Any, E : Event>(
    val source: S,
    val eventClass: KClass<E>
) : TransitionBuilder<S, E>

internal class TargetedTransitionBuilderImpl<S : Any, T : Any, E : Event>(
    source: S,
    target: T,
    eventClass: KClass<E>
) : TargetedTransitionBuilder<S, T, E>, TransitionDeclarationSource {
    override var declaration: TriggeredTransitionDeclaration =
        TriggeredTransitionDeclaration(
            source = source,
            target = target,
            triggerEventClass = eventClass as KClass<Event>
        )
        private set

    fun update(updater: (TriggeredTransitionDeclaration) -> TriggeredTransitionDeclaration): TargetedTransitionBuilder<S, T, E> {
        declaration = updater(declaration)

        return this
    }
}

internal class SelfTransitionBuilderImpl<S : Any, E : Event>(
    source: S,
    eventClass: KClass<E>
) : SelfTransitionBuilder<S, E>,
    TransitionDeclarationSource {
    override var declaration: SelfTransitionDeclaration =
        SelfTransitionDeclaration(source, eventClass as KClass<Event>)


    fun update(updater: (SelfTransitionDeclaration) -> SelfTransitionDeclaration): SelfTransitionBuilder<S, E> {
        declaration = updater(declaration)

        return this
    }
}

internal data class CompletionTransitionBuilderImpl<S : Any>(val source: S) :
    CompletionTransitionBuilder<S>

internal class TargetedCompletionTransitionBuilderImpl<S : Any, T : Any>(
    source: S,
    target: T,
) : TargetedCompletionTransitionBuilder<S, T>, TransitionDeclarationSource {

    override var declaration: CompletionTransitionDeclaration =
        CompletionTransitionDeclaration(
            source = source,
            target = target,
        )
        private set

    fun update(updater: (CompletionTransitionDeclaration) -> CompletionTransitionDeclaration): TargetedCompletionTransitionBuilder<S, T> {
        declaration = updater(declaration)

        return this
    }
}