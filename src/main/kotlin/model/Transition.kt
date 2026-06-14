package dev.timray.kotomata.model

import dev.timray.kotomata.builder.Selection

sealed interface Transition<C, E: Any>

internal data class ChoiceTransition<S, C, E, T>(
    val selector: (C, E) -> Selection<T>,
) : BranchingTransition<S,C, E, T>, HyperTransition<C, E>
        where S: Any,
              E: Event,
              T: Any

