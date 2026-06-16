package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.Region
import dev.timray.kotomata.model.TransitionAction

sealed interface Fork<S : Any> : VertexDeclaration {
    override val id: S
}

internal data class ForkDeclaration<S : Any, C>(
    override val id: S,
    override val containingStateId: Any,
    val targetIds: Map<Region, Any> = emptyMap(),
    val headActions: Map<Any, (C) -> C> = emptyMap(),
) : Fork<S>


class ForkTargetBuilder<T : Any> internal constructor(
    val region: Region,
    val target: T
)

sealed interface ForkBuilderScope<S : Any, C> {
    fun <T : Any> target(region: Region, target: T): ForkTargetBuilder<T>
    infix fun <T : Any> ForkTargetBuilder<T>.withAction(action: TransitionAction<C>)
}

internal class ForkBuilderScopeImpl<S : Any, C> internal constructor(
    val id: S,
    internal val containingStateId: Any,
) : ForkBuilderScope<S, C> {
    var declaration =
        ForkDeclaration<S, C>(id = id, containingStateId = containingStateId)

    override fun <T : Any> target(
        region: Region,
        target: T
    ): ForkTargetBuilder<T> {
        update { dec -> dec.copy(targetIds = dec.targetIds + (region to target)) }

        return ForkTargetBuilder(region = region, target)
    }

    override fun <T : Any> ForkTargetBuilder<T>.withAction(
        action: TransitionAction<C>
    ) {
        update { dec -> dec.copy(headActions = dec.headActions + (target to action)) }
    }

    private fun update(updater: (ForkDeclaration<S, C>) -> ForkDeclaration<S, C>) {
        declaration = updater(declaration)
    }
}


/*class ForkBuilderScope<S : Any, Src : Any> internal constructor(
    val id: S,
    val sourceId: Src,
    internal val containingStateId: Any,
    internal val graph: MutableXHiGraph
) {
    internal var forkDeclaration: ForkDeclaration<Any, Any, Any?> =
        ForkDeclaration(
            id = id,
            source = sourceId,
            containingStateId = containingStateId
        )
        private set

    fun <T : Any> target(region: Region, vertexId: T) {
        forkDeclaration = forkDeclaration.run {
            copy(targetIds = targetIds + (region.id to vertexId))
        }
    }

    fun <T : Any> target(region: Region, vertex: Vertex<T>) =
        target(region, vertex.id)

    fun targets(vararg pairs: Pair<Region, Any>) {
        forkDeclaration = forkDeclaration.run {
            copy(targetIds = targetIds + pairs.map { pair -> pair.first.id to pair.second })
        }
    }
}


internal fun <S : Any, Src : Any, C> MutableXHiGraph.addForkHyperTransition(dec: ForkDeclaration<S, Src, C>): ForkHyperTransition {
    val sourceNode = getOrNode(dec.source)
    require(sourceNode != null)

    val tailSet = SingletonTailVertexSet(
        node = sourceNode,
        selectionPredicate = { config ->
            val ctx =
                config.context as? C ?: return@SingletonTailVertexSet false
            dec.tailGuard?.let { guard -> guard(ctx) }
                ?: return@SingletonTailVertexSet true
        },
    )
    val headSingletons =
        getAllOrNodes().filter { node -> dec.targetIds.values.contains(node.id) }
            .map { node ->
                SingletonHeadVertexSet(
                    node = node,
                    traversalAction = { config, _ ->
                        val ctx = config.context as? C
                        require(ctx != null)
                        dec.headActions[node.id]?.let { action ->
                            config.copy(context = action(ctx))
                        }
                            ?: return@SingletonHeadVertexSet config
                    }
                )
            }.toSet()

    val headSet = StrategicCompoundHeadVertexSet<ForkHeadTraversalStrategy>(
        singletons = headSingletons,
        selectionStrategy = StaticSelectionStrategy(selector = { config, input ->
            OrthogonalCompoundHeadVertexSet(singletons = input.singletons.filter { set ->
                set.selectionPredicate?.let { pred ->
                    pred(config)
                } ?: return@filter true
            }.toSet())
        })
    )

    return ForkHyperTransition(
        tail = tailSet,
        head = headSet
    )
}

internal fun <S : Any, Src : Any, C> buildForkVertex(
    declaration: ForkDeclaration<S, Src, C>,
    graph: MutableXHiGraph,
): ForkVertexImpl<S, C> {
    return ForkVertexImpl(
        id = declaration.id,
        forkHyperTransition = graph.addForkHyperTransition(declaration)
    )
}*/