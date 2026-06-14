package dev.timray.kotomata.model

import kotlinx.coroutines.Deferred

sealed interface HyperTransition<C, E : Any> : Transition<C, E>

internal sealed interface BranchingTransition<S, C, E, T> :
    HyperTransition<C, E>
        where S : Any,
              E : Any,
              T : Any

sealed interface Branch<C, E, T>
        where E : Event,
              T : Any {
    val target: T
}

internal sealed interface TransitionHyperEdge<C, E : Event> :
    HyperTransition<C, E>, Hyperedge {
        val inboundActivities: TransitionActivitiesMap
        val outboundActivities: TransitionActivitiesMap
}

internal class StateHyperTransition<S: Any, C>(
    val triggerEvent: Event,
    sourceNode: StateNode<S>,
    targetNode: OrNode,
    override val outboundActivities: TransitionActivitiesMap,
) : TransitionHyperEdge<C, Event> {
    override val targetSet: Set<OrNode> = setOf(targetNode)
    override val sourceSet: Set<OrNode> = setOf(sourceNode)
    override val inboundActivities: TransitionActivitiesMap = emptyMap()
}

internal class InitialHyperTransition<C, T : Any>(target: VertexNode<T>) :
    TransitionHyperEdge<C, StartEvent> {
    override val sourceSet: Set<OrNode> = emptySet()
    override val targetSet: Set<OrNode> = setOf(target)
    override val inboundActivities: TransitionActivitiesMap = emptyMap()
    override val outboundActivities: TransitionActivitiesMap = emptyMap()
}

internal class FinalHyperTransition(override val sourceSet: Set<OrNode>) :
    TransitionHyperEdge<Any?, FinalEvent> {
    override val targetSet: Set<OrNode> = emptySet()
    override val inboundActivities: TransitionActivitiesMap = emptyMap()
    override val outboundActivities: TransitionActivitiesMap = emptyMap()
}


internal data class BranchImpl<S, C, E, T>(
    override val target: T,
    val guard: ((C, E) -> Boolean)? = null,
    val action: (C, E) -> Deferred<C>,
) : Branch<C, E, T>
        where S : Any,
              E : Event,
              T : Any


internal data class JunctionHyperTransition<S, C, E, T>(
    override val sourceSet: Set<OrNode>,
    override val targetSet: Set<OrNode>,
    val branches: List<Branch<C, E, T>>,
    override val inboundActivities: TransitionActivitiesMap,
    override val outboundActivities: TransitionActivitiesMap
) : BranchingTransition<S, C, E, T>, TransitionHyperEdge<C, E>, Hyperedge
        where S : Any,
              E : Event,
              T : Any

internal class ForkHyperTransition<S, C, E, T>(
   val triggerEvent: E,
    source: OrNode,
    override val targetSet: Set<OrNode>,
    override val inboundActivities: TransitionActivitiesMap,
    override val outboundActivities: TransitionActivitiesMap,
) : TransitionHyperEdge<C, E>, Hyperedge
        where S : Any,
              E : Event,
              T : Any {

    override val sourceSet: Set<OrNode> = setOf(source)
}

internal class JoinHyperTransition<S, C, T>(
    override val sourceSet: Set<OrNode>,
    target: OrNode,
    override val inboundActivities: TransitionActivitiesMap,
    override val outboundActivities: TransitionActivitiesMap
) : TransitionHyperEdge<C, JoinEvent>, Hyperedge
        where S : Any,
              T : Any {

    override val targetSet: Set<OrNode> = setOf(target)
}
