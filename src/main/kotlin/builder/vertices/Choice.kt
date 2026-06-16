package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.*

interface Selection {
    val target: Any
}

internal data class SelectionImpl<C>(
    override val target: Any,
    val action: ((C) -> C)? = null,
) : Selection

sealed interface Choice<S : Any> : VertexDeclaration {
    override val id: S
}

interface SelectionScope<C> {
    val context: C

    fun select(target: Any): Selection

    infix fun Selection.withAction(action: (C) -> C): Selection
}

internal class ChoiceBuilderScopeImpl<C>(override val context: C) :
    SelectionScope<C> {

    override fun select(target: Any): Selection = SelectionImpl<C>(target = target)

    override fun Selection.withAction(
        action: (C) -> C
    ): Selection = (this as SelectionImpl<C>).copy(action = action)

}

typealias ChoiceSelector<C> = SelectionScope<C>.() -> Selection


internal data class ChoiceDeclaration<S : Any, C>(
    override val id: S,
    override val containingStateId: Any,
    val containingRegion: Region,
    val selector: ChoiceSelector<C>
) : Choice<S>

internal fun <S : Any, Src : Any, C> MutableXHiGraph.buildChoiceHyperTransition(
    source: Src,
    declaration: ChoiceDeclaration<S, C>
): ChoiceHyperTransition {
    val sourceNode = getOrNode(source)
    require(sourceNode != null)
    return ChoiceHyperTransition(
        tail = SingletonTailVertexSet(node = sourceNode),
        head = DynamicHeadVertexSet(
            selectionStrategy = DynamicSelectionStrategy(
                selector = { config ->
                    val ctx = config.context
                    require(ctx as? C != null)
                    val selection =
                        ChoiceBuilderScopeImpl<C>(ctx).run {
                            declaration.selector.let { selector -> selector() }
                        }
                                as SelectionImpl<C>
                    val targetNode =
                        getOrNode(selection.target)
                    require(targetNode != null)

                    SingletonHeadVertexSet(
                        node = targetNode,
                        traversalAction = { config, _ ->
                            val action = selection.action
                                ?: return@SingletonHeadVertexSet config
                            val ctx = config.context as? C
                            require(ctx != null)

                            config.copy(context = action(ctx))
                        }
                    )
                }
            )
        )
    )
}

internal fun <S : Any, Src : Any, C, T : Any> MutableXHiGraph.addChoice(
    source: Src,
    declaration: ChoiceDeclaration<S, C>,
): ChoiceVertexImpl<S, C, T> {
    return ChoiceVertexImpl<S, C, T>(
        id = declaration.id,
        hyperTransition = buildChoiceHyperTransition(source, declaration)
    )
        .also { vertex ->
            val containingStateNode =
                getOrNode(declaration.containingStateId) as? CompositeStateNode<Any, Any?>
            require(containingStateNode != null)
            addOrNode(
                vertex,
                containingStateNode.payload,
                declaration.containingRegion as RegionAtom,

                )
        }
}

internal fun <C> traversalAction(
    configuration: Configuration<C>,
    event: Event
): Configuration<C> {
    return configuration
}