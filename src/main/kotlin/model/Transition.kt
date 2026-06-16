package dev.timray.kotomata.model


typealias TransitionAction<C> = (C) -> C
typealias TriggeredTransitionAction<C, E> = (C, E) -> C
typealias TransitionGuard<C> = (C) -> Boolean

sealed interface Transition<C, E: Any>


