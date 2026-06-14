package dev.timray.kotomata.model

internal sealed interface Node {
    val id: Any
    val payload: Atom
}

internal sealed interface CompoundNode: Node

internal sealed interface OrNode: Node {
    override val payload: Vertex<Any>
    val index: Int
}


internal data class GenericOrNode<out S: Any, V: Vertex<S>>(
    override val id: S,
    override val payload: V,
    override val index: Int
): OrNode

internal sealed interface AndNode: CompoundNode {
    override val payload: Region
}


internal sealed interface CompoundOrNode: CompoundNode

internal sealed interface AtomicNode: OrNode

internal typealias VertexNode<S> = GenericOrNode<S, Vertex<S>>
internal typealias StateNode<S> = GenericOrNode<S, State<S>>
internal typealias CompositeStateNode<S, C> = GenericOrNode<S, CompositeState<S, C>>
internal typealias NodeMap = Map<Any, Node>
internal typealias MutableNodeMap = MutableMap<Any, Node>
