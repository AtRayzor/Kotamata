package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.Region

interface VertexDeclaration {
    val id: Any
    val containingStateId: Any
}

internal interface InterRegionVertexDeclaration: VertexDeclaration

internal interface RegionMemberDeclaration: VertexDeclaration {
    val region: Region
}