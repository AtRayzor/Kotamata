package dev.timray.kotomata.builder

import dev.timray.kotomata.model.Event


interface Selection<T: Any> {
    val target: T
}

internal data class SelectionImpl<T: Any, C>(
    override val target: T,
    val action: ((C) -> C)? = null,
) : Selection<T>


class ChoiceBuilderScope<C> internal constructor() {
    fun <T: Any> select(target: T): Selection<T> = SelectionImpl<T, C>(target)

    infix fun <T: Any> Selection<T>.withAction(action: (C) -> C): Selection<T> =
        (this as SelectionImpl<T, C>).copy(action = action)
}


interface TransitionBuilder<S: Any, C,  E: Event> {
    infix fun <T: Any> target(target: T): TransitionContinuationBuilder

    infix fun <T: Any> choose(selector: ChoiceBuilderScope<C>): TransitionContinuationBuilder
}




interface TransitionContinuationBuilder  {
    fun target() {
    }
}

internal class TransitionBuilderImpl<S: Any, C, E: Event>: TransitionBuilder<S, C, E> {
    override fun <T : Any> target(target: T): TransitionContinuationBuilder {
        TODO("Not yet implemented")
    }

    override fun <T : Any> choose(selector: ChoiceBuilderScope<C>): TransitionContinuationBuilder {
        TODO("Not yet implemented")
    }
}