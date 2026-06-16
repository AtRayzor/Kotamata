package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.DoAction
import dev.timray.kotomata.model.EntryAction
import dev.timray.kotomata.model.ExitAction
import dev.timray.kotomata.model.Region
import kotlin.coroutines.CoroutineContext

sealed interface State<S : Any> : VertexDeclaration {
    override val id: S
}

internal interface StateDeclaration<S : Any, C> : RegionMemberDeclaration,
    State<S> {
    override val id: S
    val entryAction: EntryAction<C>?
    val doAction: DoAction<C>?
    val doContext: CoroutineContext?
    val exitAction: ExitAction<C>?
    val transitions: List<TransitionDeclaration>
}

internal data class AtomicStateDeclaration<S : Any, C>(
    override val id: S,
    override val containingStateId: Any,
    override val region: Region,
    override val entryAction: EntryAction<C>? = null,
    override val doAction: DoAction<C>? = null,
    override val exitAction: ExitAction<C>? = null,
    override val transitions: List<TransitionDeclaration> = listOf(),
    override val doContext: CoroutineContext? = null,
) : StateDeclaration<S, C> {
    companion object
}


internal data class CompositeStateDeclaration<S : Any, C>(
    override val id: S,
    override val containingStateId: Any,
    override val region: Region,
    override val entryAction: EntryAction<C>? = null,
    override val doAction: DoAction<C>? = null,
    override val exitAction: ExitAction<C>? = null,
    val regionSections: List<RegionSectionDeclaration> = emptyList(),
    val interRegionVertices: List<InterRegionVertexDeclaration> = emptyList(),
    override val transitions: List<TransitionDeclaration> = emptyList(),
    override val doContext: CoroutineContext? = null,
) : StateDeclaration<S, C> {
    companion object
}


