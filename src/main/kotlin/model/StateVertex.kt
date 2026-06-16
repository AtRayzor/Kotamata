package dev.timray.kotomata.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext

typealias EntryAction<C> = (C) -> C
typealias DoAction<C> = suspend CoroutineScope.(C) -> Deferred<C>
typealias ExitAction<C> = (C) -> C

internal sealed interface StateVertex<out S: Any>: Vertex<S>

internal sealed interface StateVertexDeclaration<out S: Any, C>: StateVertex<S> {
     val entryAction: EntryAction<C>?
     val doAction: DoAction<C>?
     val exitAction: ExitAction<C>?
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

internal data class AtomicStateVertex<out S: Any, C>(
    override val id: S,
    override val entryAction: EntryAction<C>? = null,
    override val doAction: DoAction<C>? = null,
    override val exitAction: ExitAction<C>? = null,
    override val status: StateStatus = StateStatus.Idle,
    override var stateConfig: StateConfig = StateConfig(),
) : StateVertexDeclaration<S, C>

internal data class CompositeStateVertex<out S: Any, C>(
    override val id: S,
    override val entryAction: EntryAction<C>? = null,
    override val doAction: DoAction<C>? = null,
    override val exitAction: ExitAction<C>? = null,
    override val status: StateStatus = StateStatus.Idle,
    override var stateConfig: StateConfig = StateConfig(),
) : StateVertexDeclaration<S, C>