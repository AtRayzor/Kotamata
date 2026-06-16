package dev.timray.kotomata.builder

import dev.timray.kotomata.model.OrNode
import dev.timray.kotomata.model.StateHyperTransition

internal interface VertexNodeProvider {
    val id: Any
    val containingStateId: Any
    val node: OrNode
}

internal interface RegionMemberNodeProvider: VertexNodeProvider {
    val regionId: String
}

internal class StateNodeProvider(
    override val regionId: String,
    override val id: Any,
    override val containingStateId: Any,
    override val node: OrNode,
    val transition: StateHyperTransition,
) : RegionMemberNodeProvider