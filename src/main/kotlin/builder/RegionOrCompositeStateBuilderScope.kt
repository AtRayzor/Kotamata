package dev.timray.kotomata.builder

import dev.timray.kotomata.builder.vertices.AtomicStateDeclaration
import dev.timray.kotomata.builder.vertices.Choice
import dev.timray.kotomata.builder.vertices.ChoiceDeclaration
import dev.timray.kotomata.builder.vertices.Fork
import dev.timray.kotomata.builder.vertices.ForkBuilderScope
import dev.timray.kotomata.builder.vertices.ForkBuilderScopeImpl
import dev.timray.kotomata.builder.vertices.ForkDeclaration
import dev.timray.kotomata.builder.vertices.SelectionScope
import dev.timray.kotomata.builder.vertices.Initial
import dev.timray.kotomata.builder.vertices.Selection
import dev.timray.kotomata.builder.vertices.State
import dev.timray.kotomata.builder.vertices.VertexDeclaration
import dev.timray.kotomata.model.Region


@StateMachineDsl
sealed interface RegionOrCompositeStateBuilderScope<ST : Any, CT> {
    fun <S : Any> initial(): Initial

    fun <S : ST> state(id: S): State<S>

    fun <S, C> state(
        id: S,
        builder: StateBuilderScope<S, CT>.() -> Unit
    ): State<S>
            where S : ST,
                  C : CT


    fun <S, C> compositeState(
        id: S,
        builder: CompositeStateBuilderScope<S, C>.() -> Unit
    ): State<S>
            where S : ST,
                  C : CT


    fun <S : Any> choice(
        id: S,
        selector: SelectionScope<CT>.() -> Selection
    ): Choice<S>

    fun <S : Any> fork(
        id: S,
        builder: ForkBuilderScope<S, CT>.() -> Unit
    ): Fork<S>
}

internal class RegionOrCompositeStateBuilderScopeImpl<ST, CT> internal constructor(
    val containingStateId: Any,
    val region: Region,
) :
    RegionOrCompositeStateBuilderScope<ST, CT>
        where ST : Any {
    val vertexDeclarations = mutableListOf<VertexDeclaration>()


    override fun <S : Any> initial(): Initial {
        TODO("Not yet implemented")
    }

    override fun <S : ST> state(id: S): State<S> =
        AtomicStateDeclaration<S, CT>(
            id = id,
            containingStateId = containingStateId,
            region = region
        ).also { declaration ->
            vertexDeclarations.add(declaration)
        }

    override fun <S, C> state(
        id: S,
        builder: StateBuilderScope<S, CT>.() -> Unit
    ): State<S>
            where S : ST,
                  C : CT = StateBuilderScopeImpl<S, CT>(
        id = id,
        containingStateId = containingStateId,
        region = region
    ).apply {
        builder()
    }
        .declaration
        .also { declaration ->
            vertexDeclarations.add(declaration)
        }


    override fun <S : ST, C : CT> compositeState(
        id: S,
        builder: CompositeStateBuilderScope<S, C>.() -> Unit
    ): State<S> =
        CompositeStateBuilderScopeImpl<S, C>(
            id = id,
            containingStateId = containingStateId,
            region = region
        )
            .apply { builder() }
            .stateBuilderImpl
            .declaration
            .also { declaration ->
                vertexDeclarations.add(declaration)
            }

    override fun <S : Any> choice(
        id: S,
        selector: SelectionScope<CT>.() -> Selection
    ): Choice<S> =
        ChoiceDeclaration(
            id = id,
            containingStateId = containingStateId,
            containingRegion = region,
            selector = selector
        )
            .also { declaration ->
                vertexDeclarations.add(declaration)
            }

    override fun <S : Any> fork(
        id: S,
        builder: ForkBuilderScope<S, CT>.() -> Unit
    ): Fork<S> =
        ForkBuilderScopeImpl<S, CT>(
            id = id,
            containingStateId = containingStateId
        ).apply { builder() }
            .declaration
            .also { declaration ->
                vertexDeclarations.add(declaration)
            }
}
