package dev.timray.kotomata.model

internal data class AndOrEdge(val source: AndNode, val target: OrNode)
internal data class OrAndEdge(val source: OrNode, val target: AndNode)