package dev.timray.kotomata.builder

import dev.timray.kotomata.model.Vertex



/*class TransitionFunctionsScope<C> internal constructor() {
    private var guard: ((C) -> Boolean)? = null
    private var action: ((C) -> C)? = null

    fun withGuard(guard: (C) -> Boolean) {
        this.guard = guard
    }

    fun withAction(action: (C) -> C) {
        this.action = action
    }
}


internal sealed interface TransitionDeclaration {
    data class Targeted<C, E>(
        var target: Any,
        var guard: ((C) -> Boolean)? = null,
        var action: ((C, E) -> C)? = null,
    ) : TransitionDeclaration

    data class Choice<C>(val selector: (C) -> Selection<Any>) :
        TransitionDeclaration
}



interface TransitionActivitiesBuilderScopeDepr<C, E : Event> {
    infix fun withGuard(guard: (C) -> Boolean): TransitionActivitiesBuilderScopeDepr<C, E>
    infix fun withAction(action: (C, E) -> C): TransitionActivitiesBuilderScopeDepr<C, E>
}


sealed interface TransitionBuilderDep

internal sealed interface TransitionBuilderInternal {
    fun build(): BuilderContext.() -> SingletonHeadVertexSet
}


internal class StateTransitionBuilder<T : Any, C, E : Event>(private val target: T) :
    TransitionBuilderDep,
    TransitionActivitiesBuilderScopeDepr<C, E>,
    TransitionBuilderInternal {
    private var guard: ((C) -> Boolean)? = null
    private var action: ((C, E) -> C)? = null

    override fun withGuard(guard: (C) -> Boolean): TransitionActivitiesBuilderScopeDepr<C, E> {
        this.guard = guard
        return this
    }

    override fun withAction(action: (C, E) -> C): TransitionActivitiesBuilderScopeDepr<C, E> {
        this.action = action

        return this
    }

    override fun build(): BuilderContext.() -> SingletonHeadVertexSet {
        @Suppress("UNCHECKED_CAST")
        return {
            val node = graph.getOrNode(target)
            require(node != null)
            SingletonHeadVertexSet(
                node = node,
                selectionPredicate = { config ->
                    val ctx = config.context as? C
                        ?: return@SingletonHeadVertexSet false
                    guard?.let { guard -> guard(ctx) } ?: true
                },
                traversalAction = { config, event ->
                    val action = action
                    action ?: return@SingletonHeadVertexSet config
                    val ctx = config.context
                    require(ctx as? C != null)
                    require(event as? E != null)
                    config.copy(context = action(ctx, event))
                },
            )
        }
    }
}



internal class ChoiceTransitionBuilder<S : Any, T : Any, C>(
    val source: S,
    val selector: ChoiceBuilderScope<C>.(C) -> Selection<T>
) : TransitionBuilderDep, TransitionBuilderInternal {

    override fun build(): BuilderContext.() -> SingletonHeadVertexSet {
        return {
            val sourceNode = graph.getOrNode(source)
            require(sourceNode != null)

            val transition = buildChoiceHyperTransition(sourceNode, selector)
            val vertex = ChoiceVertex<C, T>(transition)
            val node = graph.addOrNode(vertex, containingState, region)

            SingletonHeadVertexSet(node = node)
        }
    }
}

internal class ForkTransitionBuilderScope {

}

internal class ForkTransitionBuilder<S: Any>:
    TransitionBuilderDep,
    TransitionBuilderInternal{
    override fun build(): BuilderContext.() -> SingletonHeadVertexSet {
        TODO("Not yet implemented")
    }

}

interface TransitionBuilderScopeDepr<C, E : Event> {
    infix fun <T : Any> target(target: T): TransitionActivitiesBuilderScopeDepr<C, E>
    infix fun <T : Any> choose(select: ChoiceBuilderScope<C>.(C) -> Selection<T>)
}


internal class TransitionBuilderImpl<C, E : Event> :
    TransitionBuilderScopeDepr<C, E>,
    TransitionActivitiesBuilderScopeDepr<C, E> {
    var vertexSetFactory: (BuilderContext.() -> SingletonHeadVertexSet)? = null

    override fun <T : Any> target(target: T): TransitionActivitiesBuilderScopeDepr<C, E> {
        vertexSetFactory = {
            val targetNode = graph.getOrNode(target)
            require(targetNode != null)
            SingletonHeadVertexSet(
                node = targetNode,
            )
        }

        return this
    }

    override fun <T : Any> choose(select: ChoiceBuilderScope<C>.(C) -> Selection<T>) {
        declaration = TransitionDeclaration.Choice<C>(selector = { ctx ->
            @Suppress("UNCHECKED_CAST")
            ChoiceBuilderScope<C>().run { select(ctx) as Selection<Any> }
        })
    }

    override fun withGuard(guard: (C) -> Boolean): TransitionActivitiesBuilderScopeDepr<C, E> {
        val currentDeclaration = declaration
        require(currentDeclaration is TransitionDeclaration.Targeted<*, *>)
        @Suppress("UNCHECKED_CAST")
        declaration =
            (currentDeclaration as TransitionDeclaration.Targeted<C, E>).copy(
                guard = guard
            )

        return this
    }

    override fun withAction(action: (C, E) -> C): TransitionActivitiesBuilderScopeDepr<C, E> {
        val currentDeclaration = declaration
        require(currentDeclaration is TransitionDeclaration.Targeted<*, *>)
        @Suppress("UNCHECKED_CAST")
        declaration =
            (currentDeclaration as TransitionDeclaration.Targeted<C, E>).copy(
                action = action
            )

        return this
    }

    fun build() {
        val ch = Ch
    }
}*/