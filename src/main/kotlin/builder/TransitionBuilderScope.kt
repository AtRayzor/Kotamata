package dev.timray.kotomata.builder

import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.Vertex

sealed interface TransitionBuilderScope<S, E: Event>

internal data class TransitionBuilderScopeImpl<S: Any, C, E: Event>(
    val source: S
): TransitionBuilderScope<S, E>

