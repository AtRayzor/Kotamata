package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.Region

sealed interface Join: VertexDeclaration

internal data class JoinDeclaration(
    override val id: Any,
    override val containingStateId: Any,
    val sourceIds: Map<Region, Any>,
    val targetId: Any,
    val targetRegion: Region,
): Join