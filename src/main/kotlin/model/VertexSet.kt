package dev.timray.kotomata.model

internal sealed interface VertexSet {
    val nodes: Set<OrNode>
}

internal sealed class EmptyVertexSet : VertexSet {
    override val nodes: Set<OrNode> = emptySet()
}

internal sealed interface TailVertexSet : VertexSet

internal sealed interface HeadVertexSet : VertexSet

internal sealed interface SingletonVertexSet : VertexSet {
    val node: OrNode
    val selectionPredicate: ((Configuration<Any?>) -> Boolean)?
    val traversalAction: ((Configuration<Any?>, Event) -> Configuration<*>)?
}

internal sealed interface CompoundVertexSet : VertexSet {
    val singletons: Set<SingletonVertexSet>
}

internal interface StrategicCompoundVertexSet<S> : CompoundVertexSet
        where S : CompoundSelectionStrategy {
    val selectionStrategy: S
}

internal data class SingletonTailVertexSet(
    override val node: OrNode,
    override val traversalAction: ((Configuration<Any?>, Event) -> Configuration<*>)? = null,
    override val selectionPredicate: ((Configuration<Any?>) -> Boolean)? = null,
) : TailVertexSet, SingletonVertexSet {
    override val nodes: Set<OrNode> = setOf(node)
}

internal data class SingletonHeadVertexSet(
    override val node: OrNode,
    override val selectionPredicate: ((Configuration<Any?>) -> Boolean)? = null,
    override val traversalAction: ((Configuration<Any?>, Event) -> Configuration<*>)? = null,
) : HeadVertexSet, SingletonVertexSet {
    override val nodes: Set<OrNode> = setOf(node)
}

internal sealed interface CompoundTailVertexSet : CompoundVertexSet,
    TailVertexSet

internal data class StrategicCompoundTailVertexSet<S>(
    override val singletons: Set<SingletonVertexSet>,
    override val selectionStrategy: S,
) : StrategicCompoundVertexSet<S>, CompoundTailVertexSet
        where S : CompoundSelectionStrategy {
    override val nodes: Set<OrNode> =
        singletons.map { s -> s.node }.toSet()
}

internal sealed interface CompoundHeadVertexSet : CompoundVertexSet,
    HeadVertexSet

internal sealed interface OrthogonalCompoundVertexSet : CompoundVertexSet

internal data class StaticCompoundHeadVertexSet(
    override val singletons: Set<SingletonVertexSet>,

    ) : CompoundHeadVertexSet {
    override val nodes: Set<OrNode> = singletons.map { set -> set.node }.toSet()
}

internal data class StrategicCompoundHeadVertexSet<S>(
    override val singletons: Set<SingletonVertexSet>,
    override val selectionStrategy: S,
) : CompoundHeadVertexSet, StrategicCompoundVertexSet<S>
        where S : CompoundSelectionStrategy {
    override val nodes: Set<OrNode> =
        singletons.map { s -> s.node }.toSet()
}

internal data class DynamicHeadVertexSet(
    val selectionStrategy: DynamicSelectionStrategy<SingletonHeadVertexSet>,
) : HeadVertexSet, CompoundHeadVertexSet {
    override val singletons: Set<SingletonHeadVertexSet> = emptySet()
    override val nodes: Set<OrNode> = emptySet()
}

internal data class OrthogonalCompoundTailVertexSet(
    override val singletons: Set<SingletonVertexSet>,
    override val nodes: Set<OrNode>
) : OrthogonalCompoundVertexSet, TailVertexSet


internal data class OrthogonalCompoundHeadVertexSet(
    override val singletons: Set<SingletonVertexSet>,
) : OrthogonalCompoundVertexSet, HeadVertexSet {
    override val nodes: Set<OrNode> = singletons.map { set -> set.node }.toSet()
}

internal object EmptyTailVertexSet : EmptyVertexSet(), TailVertexSet
internal object EmptyHeadVertexSet : EmptyVertexSet(), HeadVertexSet
