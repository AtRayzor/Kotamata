package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.TransitionAction
import dev.timray.kotomata.model.TransitionGuard
import dev.timray.kotomata.model.TriggeredTransitionAction
import kotlin.reflect.KClass

interface TransitionDeclaration {
    val source: Any
}

interface TargetedTransitionDeclaration : TransitionDeclaration {
    val target: Any
}

internal data class SelfTransitionDeclaration(
    override val source: Any,
    val triggerEventClass: KClass<Event>,
    val guard: TransitionGuard<Any?>? = null,
    val action: TriggeredTransitionAction<Any?, Event>? = null
) : TransitionDeclaration

internal data class CompletionTransitionDeclaration(
    override val target: Any,
    override val source: Any,
    val action: TransitionAction<Any?>? = null
): TargetedTransitionDeclaration

internal data class TriggeredTransitionDeclaration(
    override val source: Any,
    override val target: Any,
    val triggerEventClass: KClass<Event>,
    val guard: TransitionGuard<Any?>? = null,
    val action: TriggeredTransitionAction<Any?, Event>? = null
) : TargetedTransitionDeclaration
