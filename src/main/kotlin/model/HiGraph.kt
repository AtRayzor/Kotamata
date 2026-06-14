package dev.timray.kotomata.model

import kotlin.concurrent.fixedRateTimer

internal sealed interface HiGraph

internal class MutableHiGraph(
    rootState: CompositeState<String, Any?>
) : HiGraph {
    private var orNodeIndex = -1
    val rootNode: OrNode = GenericOrNode(
        id = rootState.id,
        payload = rootState,
        index = nextOrNodeIndex()
    )
    val nodeMap: MutableNodeMap = mutableMapOf()
    val hyperedges: MutableSet<Hyperedge> = mutableSetOf()
    val andOrEdges: MutableSet<AndOrEdge> = mutableSetOf()
    val orAndEdges: MutableSet<OrAndEdge> = mutableSetOf()
    val lcaTable: LCAMap = precomputeLcaTable()

    init {
        nodeMap[rootNode.id] = rootNode
    }

    fun nextOrNodeIndex() = ++orNodeIndex
}


internal fun HiGraph.getAndNode(
    state: CompositeState<*, *>,
    region: Region
): AndNode? {
    val node = getNode(state.id) ?: return null

    return andNodeSuccessors(node).singleOrNull { node -> node.payload == region }
}

internal fun HiGraph.getAndNode(node: OrNode, region: Region) =
    andNodeSuccessors(node).singleOrNull { node -> node.payload == region }


internal fun MutableHiGraph.addNode(node: Node) {
    nodeMap[node.id] = node
}

internal fun <S : Any, N : OrNode> MutableHiGraph.addOrNodeGeneric(
    vertex: Vertex<S>,
    compositeState: CompositeState<Any, Any?>,
    region: Region
): N {
    val node = VertexNode(
        id = vertex.id,
        payload = vertex,
        index = nextOrNodeIndex()
    )

    val containingRegionNode =
        getAndNode(compositeState, region).let { regionNode ->
            if (regionNode != null) {
                return@let regionNode
            }

            val compositeStateNode = getOrNode(compositeState.id)
                    as? StateNode<Any>
                ?: error("Can't find composite state for $vertex")
            val rNode = addAndNode(region, compositeStateNode)
            addOrAndEdge(compositeStateNode, rNode)
            rNode
        }

    addNode(node)
    addAndOrEdge(containingRegionNode, node)

    return node as N
}

internal fun <S : Any> MutableHiGraph.addOrNode(
    vertex: Vertex<S>,
    compositeState: CompositeState<Any, Any?>,
    region: Region
) = addOrNodeGeneric<S, OrNode>(vertex, compositeState, region)

internal fun <S : Any, C> MutableHiGraph.addCompositeStateNode(
    state: CompositeState<S, C>,
    compositeState: CompositeState<Any, Any?>,
    region: Region
) = addOrNodeGeneric<S, CompositeStateNode<S, C>>(state, compositeState, region)


internal fun MutableHiGraph.addAndNode(
    region: Region,
    containingStateNode: StateNode<Any>,
): AndNode {
    val node = region.createNode()
    addNode(node)
    addOrAndEdge(containingStateNode, node)

    return node
}

internal fun MutableHiGraph.removeNode(node: Node) {
    nodeMap.remove(node.id)
}

internal fun MutableHiGraph.addHyperEdges(vararg hyperedges: Hyperedge) {
    this.hyperedges.addAll(hyperedges)
}

internal fun MutableHiGraph.removeHyperedges(vararg hyperedges: Hyperedge) {
    this.hyperedges.removeAll(hyperedges.toSet())
}

internal fun MutableHiGraph.addAndOrEdge(source: AndNode, target: OrNode) {
    andOrEdges.add(AndOrEdge(source, target))
}


internal fun MutableHiGraph.addOrAndEdge(source: OrNode, target: AndNode) {
    orAndEdges.add(OrAndEdge(source, target))
}

internal fun HiGraph.getNode(id: Any): Node? =
    (this as MutableHiGraph).nodeMap[id]

internal fun HiGraph.getOrNode(id: Any): OrNode? =
    (this as MutableHiGraph).nodeMap[id] as OrNode?

internal fun HiGraph.getAndNode(id: Any): AndNode? =
    (this as MutableHiGraph).nodeMap[id] as AndNode?


internal fun HiGraph.incomingHyperEdges(node: Node): List<Hyperedge> =
    (this as MutableHiGraph).hyperedges.filter { hyperedge ->
        hyperedge.targetSet.contains(
            node
        )
    }

internal fun HiGraph.incomingHyperEdges(
    node: Node,
    predicate: (Hyperedge) -> Boolean
): List<Hyperedge> =
    incomingHyperEdges(node).filter { predicate.invoke(it) }

internal fun HiGraph.outgoingHyperEdges(node: Node): List<Hyperedge> =
    (this as MutableHiGraph).hyperedges.filter { hyperedge ->
        hyperedge.sourceSet.contains(
            node
        )
    }

internal fun HiGraph.outgoingHyperEdges(
    node: Node,
    predicate: (Hyperedge) -> Boolean
): List<Hyperedge> =
    outgoingHyperEdges(node).filter { predicate.invoke(it) }

internal fun HiGraph.singleIncomingHyperEdge(
    node: Node,
    predicate: (Hyperedge) -> Boolean
): Hyperedge? =
    incomingHyperEdges(node).singleOrNull { predicate.invoke(it) }

internal fun HiGraph.singleOutgoingHyperEdge(
    node: Node,
    predicate: (Hyperedge) -> Boolean
): Hyperedge? =
    outgoingHyperEdges(node).singleOrNull { predicate.invoke(it) }

internal fun HiGraph.hyperEdgePredecessors(node: Node): List<Node> =
    (this as MutableHiGraph).incomingHyperEdges(node)
        .flatMap { edge -> edge.sourceSet }


internal fun HiGraph.hyperEdgeSuccessors(node: Node): List<Node> =
    (this as MutableHiGraph).outgoingHyperEdges(node)
        .flatMap { edge -> edge.targetSet }

internal fun HiGraph.hyperInDegree(node: Node) = incomingHyperEdges(node).size

internal fun HiGraph.hyperOutDegree(node: Node) = outgoingHyperEdges(node).size

internal fun HiGraph.getOutgoingEdges(node: Node): List<AndOrEdge> =
    (this as MutableHiGraph).andOrEdges.filter { edge -> edge.source == node }

internal fun HiGraph.getIncomingEdges(node: AndNode): List<OrAndEdge> =
    (this as MutableHiGraph).orAndEdges.filter { edge -> edge.target == node }


internal fun HiGraph.getOutgoingEdges(node: OrNode): List<OrAndEdge> =
    (this as MutableHiGraph).orAndEdges.filter { edge -> edge.source == node }

internal fun HiGraph.getIncomingEdges(node: OrNode): List<AndOrEdge> =
    (this as MutableHiGraph).andOrEdges.filter { edge -> edge.target == node }

internal fun HiGraph.andNodeSuccessors(node: Node): List<AndNode> {
    val impl = this as MutableHiGraph

    return when (node) {
        is OrNode -> impl.getOutgoingEdges(node).map { edge -> edge.target }
        is AndNode -> impl.getOutgoingEdges(node).flatMap { edge ->
            impl.getOutgoingEdges(edge.target).map { it.target }
        }
    }
}

internal fun HiGraph.orNodeSuccessors(node: Node): List<OrNode> {
    val impl = this as MutableHiGraph

    return when (node) {
        is AndNode -> impl.getOutgoingEdges(node).map { edge -> edge.target }
        is OrNode -> impl.getOutgoingEdges(node).flatMap { edge ->
            impl.getOutgoingEdges(edge.target).map { it.target }
        }
    }
}

internal fun HiGraph.andNodePredecessors(node: Node): List<AndNode> {
    val impl = this as MutableHiGraph

    return when (node) {
        is OrNode -> impl.getIncomingEdges(node).map { edge -> edge.source }
        is AndNode -> impl.getIncomingEdges(node).flatMap { edge ->
            impl.getIncomingEdges(edge.source).map { it.source }
        }
    }
}

internal fun HiGraph.orNodePredecessors(node: Node): List<OrNode> {
    val impl = this as MutableHiGraph

    return when (node) {
        is AndNode -> impl.getIncomingEdges(node).map { edge -> edge.source }
        is OrNode -> impl.getIncomingEdges(node).flatMap { edge ->
            impl.getIncomingEdges(edge.source).map { it.source }
        }
    }
}


internal fun HiGraph.getAllNodes() =
    (this as MutableHiGraph).nodeMap.values.toList()

internal fun HiGraph.getAllOrNodes() =
    (this as MutableHiGraph).nodeMap.values.filterIsInstance<OrNode>().toList()

internal fun HiGraph.getAllAndNodes() =
    (this as MutableHiGraph).nodeMap.values.filterIsInstance<AndNode>().toList()

internal fun HiGraph.getPathToRoot(node: OrNode): List<OrNode> =
    generateSequence(node) { n -> orNodePredecessor(n) }.toList()

internal fun HiGraph.getRoot(): OrNode = (this as MutableHiGraph).rootNode


internal fun HiGraph.getOrNodes() =
    (this as MutableHiGraph).nodeMap.values.filterIsInstance<OrNode>()

internal fun HiGraph.orNodePredecessor(node: Node): OrNode? =
    orNodePredecessors(node).singleOrNull()

internal fun HiGraph.andNodePredecessor(node: Node): AndNode? =
    andNodePredecessors(node).singleOrNull()

internal fun HiGraph.orNodePathToRoot(orNode: OrNode): List<Node> =
    generateSequence(orNode) { node -> orNodePredecessor(node) }.toList()

internal fun HiGraph.getLca(node1: OrNode, node2: AndNode) =
    (this as MutableHiGraph).lcaTable[setOf(node1, node2)]
        ?: error("Invalid input nodes")


internal fun HiGraph.getLca(node1: OrNode) =
    (this as MutableHiGraph).lcaTable[setOf(node1)]
        ?: error("Invalid input nodes")

context(graph: HiGraph)
internal fun OrNode.getContainingRegionNode() =
    graph.andNodePredecessor(this)


private typealias MutableLCAMap = MutableMap<Set<Any>, OrNode>
internal typealias LCAMap = Map<Set<Any>, OrNode>

context(graph: HiGraph)
internal fun precomputeLcaTable(): LCAMap = with(graph) {
    val map: MutableLCAMap = mutableMapOf()
    val nodeList = buildArray(graph.getRoot())

    if (nodeList.isEmpty()) {
        return@with map
    }

    for (len in 1..nodeList.size) {
        var startIndex = 0
        var endIndex = len

        while (endIndex < nodeList.size) {
            val slice = nodeList.slice(startIndex until endIndex)
            val lca = slice.minBy { node -> node.index }

            startIndex++
            endIndex = startIndex + len



            val firstNode = slice.first()
            val lastNode = slice.last()
            val set = if(firstNode != lastNode) setOf(firstNode, lastNode) else setOf(firstNode)

            if(map.containsKey(set)) {
                continue
            }

            map[set] = lca
        }
    }

    map
}

context(graph: HiGraph)
private  fun buildArray(node: OrNode): List<OrNode> {
    var children = graph.orNodeSuccessors(node)
    val mutableList = mutableListOf<OrNode>()

    mutableList.add(node)

    while (children.isNotEmpty()) {
        val current = children.first()
        children = children.drop(1)
        mutableList.addAll(buildArray(current))
        mutableList.add(node)
    }

    return mutableList
}

internal fun <A, B> Pair<A, B>.reversed(): Pair<B, A> = second to first