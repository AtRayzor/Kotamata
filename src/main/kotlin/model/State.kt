package dev.timray.kotomata.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext

sealed interface State<out S: Any>: Vertex<S>

internal sealed interface StateDeclaration<out S: Any, C>: State<S> {
     val entryAction: ((C) -> C)?
     val doAction: (suspend CoroutineScope.(C) -> Deferred<C>)?
     val exitAction: ((C) -> C)?
     val status: StateStatus
     var stateConfig: StateConfig
}

internal sealed interface StateStatus {
    object Idle : StateStatus
    data class Running(val workActionDeferred: Deferred<Any?>) : StateStatus
}

internal data class StateConfig(
    val coroutineContext: CoroutineContext? = null,
)

internal data class AtomicState<out S: Any, C>(
    override val id: S,
    override val entryAction: ((C) -> C)? = null,
    override val doAction: (suspend CoroutineScope.(C) -> Deferred<C>)? = null,
    override val exitAction: ((C) -> C)? = null,
    override val status: StateStatus = StateStatus.Idle,
    override var stateConfig: StateConfig = StateConfig(),
) : StateDeclaration<S, C>

internal data class CompositeState<out S: Any, C>(
    override val id: S,
    override val entryAction: ((C) -> C)? = null,
    override val doAction: (suspend CoroutineScope.(C) -> Deferred<C>)? = null,
    override val exitAction: ((C) -> C)? = null,
    override val status: StateStatus = StateStatus.Idle,
    override var stateConfig: StateConfig = StateConfig(),
) : StateDeclaration<S, C>