package dev.timray.kotomata.model

internal data class TransitionActivities<C, E: Event>(
    val guard: ((C) -> Boolean)? = null,
    val action: ((C, E) -> C)? = null,
)

internal typealias TransitionActivitiesMap = Map<Any, TransitionActivities<Any?, Event>>