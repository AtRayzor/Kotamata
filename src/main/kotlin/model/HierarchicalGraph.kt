package dev.timray.kotomata.model

internal sealed interface HierarchicalGraph

internal sealed interface MutableHierarchicalGraph: HierarchicalGraph {
    val rootNode: OrNode
    val nodeMap: MutableNodeMap
    val andOrEdges: MutableSet<AndOrEdge>
    val orAndEdges: MutableSet<OrAndEdge>
    val lcaTable: LCAMap

    fun nextOrNodeIndex(): Int
}

internal fun HierarchicalGraph.getAndNode(
    state: CompositeStateVertex<*, *>,
    region: RegionAtom
): AndNode? {
    val node = getNode(state.id) ?: return null

    return andNodeSuccessors(node).singleOrNull { node -> node.payload == region }
}

internal fun XHiGraph.getAndNode(node: OrNode, region: RegionAtom) =
    andNodeSuccessors(node).singleOrNull { node -> node.payload == region }

internal fun MutableHierarchicalGraph.addNode(node: Node) {
    nodeMap[node.id] = node
}

internal fun MutableHierarchicalGraph.getAndNode() {

}

internal fun <S : Any, N : OrNode> MutableHierarchicalGraph.addOrNodeGeneric(
    vertex: Vertex<S>,
    compositeState: CompositeStateVertex<Any, Any?>,
    region: RegionAtom
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

internal fun <S : Any> MutableHierarchicalGraph.addOrNode(
    vertex: Vertex<S>,
    compositeState: CompositeStateVertex<Any, Any?>,
    region: RegionAtom
) = addOrNodeGeneric<S, OrNode>(vertex, compositeState, region)

internal fun <S : Any, C> MutableHierarchicalGraph.addCompositeStateNode(
    state: CompositeStateVertex<S, C>,
    compositeState: CompositeStateVertex<Any, Any?>,
    region: RegionAtom
) = addOrNodeGeneric<S, CompositeStateNode<S, C>>(state, compositeState, region)

internal fun MutableHierarchicalGraph.addAndNode(
    region: RegionAtom,
    containingStateNode: StateNode<Any>,
): AndNode {
    val node = region.createNode()
    addNode(node)
    addOrAndEdge(containingStateNode, node)

    return node
}

internal fun MutableHierarchicalGraph.removeNode(node: Node) {
    nodeMap.remove(node.id)
}


internal fun MutableHierarchicalGraph.addAndOrEdge(source: AndNode, target: OrNode) {
    andOrEdges.add(AndOrEdge(source, target))
}

internal fun MutableHierarchicalGraph.addOrAndEdge(source: OrNode, target: AndNode) {
    orAndEdges.add(OrAndEdge(source, target))
}

internal fun HierarchicalGraph.getNode(id: Any): Node? =
    (this as MutableHierarchicalGraph).nodeMap[id]

internal fun HierarchicalGraph.getOrNode(id: Any): OrNode? =
    (this as MutableHierarchicalGraph).nodeMap[id] as OrNode?

internal fun HierarchicalGraph.getAndNode(id: Any): AndNode? =
    (this as MutableHierarchicalGraph).nodeMap[id] as AndNode?

internal fun HierarchicalGraph.getOutgoingEdges(node: Node): List<AndOrEdge> =
    (this as MutableHierarchicalGraph).andOrEdges.filter { edge -> edge.source == node }

internal fun HierarchicalGraph.getIncomingEdges(node: AndNode): List<OrAndEdge> =
    (this as MutableHierarchicalGraph).orAndEdges.filter { edge -> edge.target == node }


internal fun HierarchicalGraph.getOutgoingEdges(node: OrNode): List<OrAndEdge> =
    (this as MutableHierarchicalGraph).orAndEdges.filter { edge -> edge.source == node }

internal fun HierarchicalGraph.getIncomingEdges(node: OrNode): List<AndOrEdge> =
    (this as MutableHierarchicalGraph).andOrEdges.filter { edge -> edge.target == node }

internal fun HierarchicalGraph.andNodeSuccessors(node: Node): List<AndNode> {
    val impl = this as MutableHierarchicalGraph

    return when (node) {
        is OrNode -> impl.getOutgoingEdges(node).map { edge -> edge.target }
        is AndNode -> impl.getOutgoingEdges(node).flatMap { edge ->
            impl.getOutgoingEdges(edge.target).map { it.target }
        }
    }
}

internal fun HierarchicalGraph.orNodeSuccessors(node: Node): List<OrNode> {
    val impl = this as MutableHierarchicalGraph

    return when (node) {
        is AndNode -> impl.getOutgoingEdges(node).map { edge -> edge.target }
        is OrNode -> impl.getOutgoingEdges(node).flatMap { edge ->
            impl.getOutgoingEdges(edge.target).map { it.target }
        }
    }
}

internal fun HierarchicalGraph.andNodePredecessors(node: Node): List<AndNode> {
    val impl = this as MutableHierarchicalGraph

    return when (node) {
        is OrNode -> impl.getIncomingEdges(node).map { edge -> edge.source }
        is AndNode -> impl.getIncomingEdges(node).flatMap { edge ->
            impl.getIncomingEdges(edge.source).map { it.source }
        }
    }
}

internal fun HierarchicalGraph.orNodePredecessors(node: Node): List<OrNode> {
    val impl = this as MutableHierarchicalGraph

    return when (node) {
        is AndNode -> impl.getIncomingEdges(node).map { edge -> edge.source }
        is OrNode -> impl.getIncomingEdges(node).flatMap { edge ->
            impl.getIncomingEdges(edge.source).map { it.source }
        }
    }
}


internal fun HierarchicalGraph.getAllNodes() =
    (this as MutableHierarchicalGraph).nodeMap.values.toList()

internal fun HierarchicalGraph.getAllOrNodes() =
    (this as MutableHierarchicalGraph).nodeMap.values.filterIsInstance<OrNode>().toList()

internal fun HierarchicalGraph.getAllAndNodes() =
    (this as MutableHierarchicalGraph).nodeMap.values.filterIsInstance<AndNode>().toList()

internal fun HierarchicalGraph.getPathToRoot(node: OrNode): List<OrNode> =
    generateSequence(node) { n -> orNodePredecessor(n) }.toList()

internal fun HierarchicalGraph.getRoot(): OrNode = (this as MutableHierarchicalGraph).rootNode


internal fun HierarchicalGraph.getOrNodes() =
    (this as MutableHierarchicalGraph).nodeMap.values.filterIsInstance<OrNode>()

internal fun HierarchicalGraph.orNodePredecessor(node: Node): OrNode? =
    orNodePredecessors(node).singleOrNull()

internal fun HierarchicalGraph.andNodePredecessor(node: Node): AndNode? =
    andNodePredecessors(node).singleOrNull()

internal fun HierarchicalGraph.orNodePathToRoot(orNode: OrNode): List<Node> =
    generateSequence(orNode) { node -> orNodePredecessor(node) }.toList()

internal fun HierarchicalGraph.getLca(node1: OrNode, node2: AndNode) =
    (this as MutableHierarchicalGraph).lcaTable[setOf(node1, node2)]
        ?: error("Invalid input nodes")


internal fun HierarchicalGraph.getLca(node1: OrNode) =
    (this as MutableHierarchicalGraph).lcaTable[setOf(node1)]
        ?: error("Invalid input nodes")

context(graph: HierarchicalGraph)
internal fun OrNode.getContainingRegionNode() =
    graph.andNodePredecessor(this)


private typealias MutableLCAMap = MutableMap<Set<Any>, OrNode>
internal typealias LCAMap = Map<Set<Any>, OrNode>

context(graph: HierarchicalGraph)
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

context(graph: HierarchicalGraph)
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
