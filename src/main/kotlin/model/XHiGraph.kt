package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId

internal sealed interface XHiGraph: HierarchicalGraph, XHyperGraph {
    companion object
}

internal class MutableXHiGraph(
    rootState: CompositeStateVertex<String, Any?>
) : XHiGraph, MutableHierarchicalGraph, MutableXHyperGraph {
    private var orNodeIndex = -1
    override val rootNode: OrNode = GenericOrNode(
        id = rootState.id,
        payload = rootState,
        index = nextOrNodeIndex()
    )
    override val nodeMap: MutableNodeMap = mutableMapOf()
    override val hyperedges: MutableSet<XHyperEdge> = mutableSetOf()
    override val andOrEdges: MutableSet<AndOrEdge> = mutableSetOf()
    override val orAndEdges: MutableSet<OrAndEdge> = mutableSetOf()
    override val lcaTable: LCAMap = precomputeLcaTable()

    init {
        nodeMap[rootNode.id] = rootNode
    }

    override fun nextOrNodeIndex() = ++orNodeIndex
}

internal val XHiGraph.Companion.empty
    get() = MutableXHiGraph(rootState = CompositeStateVertex(id = generateRandomId()))