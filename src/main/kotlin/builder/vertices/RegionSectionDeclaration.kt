package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.Region

internal data class RegionSectionDeclaration(
    val id: String,
    val region: Region,
    val containingVertices: List<RegionMemberDeclaration>
)