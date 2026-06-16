package dev.timray.kotomata.model

internal sealed interface XHyperEdge {
    val tail: TailVertexSet
    val head: HeadVertexSet
}