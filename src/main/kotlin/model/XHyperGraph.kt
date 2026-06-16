package dev.timray.kotomata.model

import java.util.function.Predicate

internal sealed interface XHyperGraph

internal sealed interface MutableXHyperGraph: XHyperGraph {
    val nodeMap: MutableNodeMap
    val hyperedges: MutableSet<XHyperEdge>
}

internal fun  MutableXHyperGraph.addHyperEdges(vararg hyperedges: XHyperEdge) {
    this.hyperedges.addAll(hyperedges)
}

internal fun  MutableXHyperGraph.removeHyperedges(vararg hyperedges: XHyperEdge) {
    this.hyperedges.removeAll(hyperedges.toSet())
}

internal fun  XHyperGraph.getStaticIncomingEdges(node: OrNode): Set<XHyperEdge> =
    filterHeads { head -> head.nodes.contains(node) }.toSet()

internal fun  XHyperGraph.getStaticIncomingEdges(node: OrNode, predicate: (HeadVertexSet) -> Boolean): Set<XHyperEdge> =
    filterHeads { head -> head.nodes.contains(node) && predicate(head) }.toSet()

internal fun  XHyperGraph.getStaticOutgoingEdges(node: OrNode): Set<XHyperEdge> =
    filterTails { tail -> tail.nodes.contains(node) }.toSet()

internal fun  XHyperGraph.getStaticOutgoingEdges(node: OrNode, predicate: (TailVertexSet) -> Boolean): Set<XHyperEdge> =
    filterTails { tail -> tail.nodes.contains(node) && predicate(tail) }.toSet()

internal fun XHyperGraph.filterTails(predicate: (TailVertexSet) -> Boolean) =
    (this as MutableXHyperGraph).hyperedges.filter { edge -> predicate(edge.tail) }

internal fun XHyperGraph.filterHeads(predicate: (HeadVertexSet) -> Boolean) =
    (this as MutableXHyperGraph).hyperedges.filter {edge -> predicate(edge.head) }