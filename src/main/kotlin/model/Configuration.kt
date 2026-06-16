package dev.timray.kotomata.model

import kotlinx.coroutines.CoroutineScope

enum class StateMachineStatus {
    LOADING,
    STABLE,
    TRANSITIONING,
    FAULTED
}

internal data class Configuration<C>(
    val graph: XHiGraph,
    val status: StateMachineStatus,
    val coroutineScope: CoroutineScope,
    val context: C
)
