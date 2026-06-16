package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId

sealed interface PseudoState<out S : Any> : Vertex<S>

internal sealed interface InitialVertex : PseudoState<String>
internal sealed interface ChoiceVertex<S : Any, C, T : Any> : PseudoState<S>
internal sealed interface ForkVertex<S : Any, C> : PseudoState<S>
internal sealed interface JoinVertex<S : Any, C> : PseudoState<S>
internal sealed interface ShallowHistoryVertex<S : Any, T : Any> : PseudoState<S>
internal sealed interface DeepHistoryVertex<S : Any, T : Any> : PseudoState<S>
internal sealed interface Final : PseudoState<Nothing>

internal sealed interface PseudoStateNode<S : Any> : PseudoState<S>


internal class InitialImpl(
    val initialHyperTransition: InitialHyperTransition
) : InitialVertex {
    override val id: String = generateRandomId()
}

internal data class ChoiceVertexImpl<S : Any, C, T : Any>(
    override val id: S,
    val hyperTransition: ChoiceHyperTransition
) : ChoiceVertex<S, C, T>

internal data class ForkVertexImpl<S : Any, C>(
    override val id: S,
    val forkHyperTransition: ForkHyperTransition,
) : ForkVertex<S, C>, PseudoStateNode<S>

internal data class JoinVertexImpl<S : Any, T : Any>(
    override val id: S,
    val joinHyperTransition: JoinHyperTransition,
) : JoinVertex<S, T>,
    PseudoStateNode<S>

internal data class FinalVertexImpl(
    override val id: Nothing,
) : Final, PseudoStateNode<Nothing>