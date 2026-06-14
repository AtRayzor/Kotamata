package dev.timray.kotomata.model

import dev.timray.kotomata.builder.Selection
import dev.timray.kotomata.utils.generateRandomId

sealed interface PseudoState<S : Any> : Vertex<S>

sealed interface Initial : PseudoState<String>
sealed interface Choice<S : Any, C, T : Any> : PseudoState<S>
sealed interface Fork<S : Any, C> : PseudoState<S>
sealed interface Join<S : Any, C> : PseudoState<S>
sealed interface ShallowHistory<S : Any, T : Any> : PseudoState<S>
sealed interface DeepHistory<S : Any, T : Any> : PseudoState<S>
sealed interface Final : PseudoState<Nothing>

internal sealed interface PseudoStateNode<S : Any> : PseudoState<S>


internal class InitialNode: Initial {
    override val id: String = generateRandomId()
}

internal data class ChoiceNode<S : Any, C, T : Any>(
    override val id: S,
    val selector: (C, Event) -> Selection<T>,
) : Choice<S, C, T>

internal data class ForkNode<S : Any, C, T : Any>(
    override val id: S,
) : Fork<S, T>, PseudoStateNode<S>

internal data class JoinNode<S : Any, C, T : Any>(
    override val id: S,
) : Fork<S, T>,
    PseudoStateNode<S>

internal data class FinalNode(
    override val id: Nothing,
) : Final, PseudoStateNode<Nothing>