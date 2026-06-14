package dev.timray.kotomata.builder

import dev.timray.kotomata.model.RegionNode
import dev.timray.kotomata.model.AtomicState
import dev.timray.kotomata.model.State
import dev.timray.kotomata.model.StateNode


@StateMachineDsl
sealed interface StateMachineOrCompositeStateBuilderScope<ST: Any, CT> {
    fun <S: ST> state(id: S): State<S>

    fun <S, C> state(id: S, builder: StateBuilderScope<S, CT>.() -> Unit): State<S>
            where S: ST,
                  C: CT


    fun <S, C> compositeState(id: S, builder: CompositeStateBuilderScope<S, C>.() -> Unit): State<S>
            where S: ST,
                  C: CT
}

class StateMachineBuilderScope<S: Any, C> internal constructor(regionNode: RegionNode)
    : StateMachineOrCompositeStateBuilderScope<S, C> by StateMachineBuilderOrCompositeStateBuilderScopeImpl(regionNode)


internal class StateMachineBuilderOrCompositeStateBuilderScopeImpl<ST, CT> internal constructor(
    private val regionNode: RegionNode,
):
    StateMachineOrCompositeStateBuilderScope<ST, CT>
    where ST: Any
{
    var initialState: State<ST>? = null

    val stateNodes = mutableListOf<StateNode<ST>>()

    override fun <S: ST> state(id: S): State<S> {
       return AtomicState<S, CT>(id = id).also {
           //stateNodes.add(it)
       }
    }

    override fun <S, C> state(id: S, builder: StateBuilderScope<S, CT>.() -> Unit): State<S>
    where S: ST,
          C: CT=
        SimpleStateBuilderScopeImpl<S, CT>(id)
            .apply {
                builder()
            }
            .build()
            .also {node ->
                // stateNodes.add(node)
            }


    override fun <S : ST, C : CT> compositeState(
        id: S,
        builder: CompositeStateBuilderScope<S, C>.() -> Unit
    ): State<S> =
        CompositeStateBuilderScopeImpl<S, C>(id, regionNode)
            .apply {
                builder()
            }
            .build()
            .also {node ->
                stateNodes.add(node as StateNode<ST>)
            }

}
