package dev.timray.kotomata.model

internal sealed interface HyperTransition : XHyperEdge

internal typealias ChoiceHeadSelectionStrategy = DynamicSelectionStrategy<SingletonHeadVertexSet>

internal class ChoiceHyperTransition(
    override val tail: SingletonTailVertexSet,
    override val head: DynamicHeadVertexSet,
) : HyperTransition


internal typealias StateHeadSelectionStrategy = StaticSelectionStrategy<CompoundHeadVertexSet, SingletonHeadVertexSet>

internal class StateHyperTransition(
    override val tail: SingletonTailVertexSet,
    override val head: StrategicCompoundHeadVertexSet<StateHeadSelectionStrategy>,
) : HyperTransition

internal class InitialHyperTransition(
    override val tail: SingletonTailVertexSet,
    override val head: SingletonHeadVertexSet,
) : HyperTransition

internal class FinalHyperTransition(
    override val tail: TailVertexSet,
    override val head: SingletonHeadVertexSet,
) : HyperTransition


internal typealias JunctionTailSelectionStrategy = StaticSelectionStrategy<CompoundTailVertexSet, SingletonTailVertexSet>
internal typealias JunctionHeadSelectionStrategy = StaticSelectionStrategy<CompoundHeadVertexSet, SingletonHeadVertexSet>

internal data class JunctionHyperTransition(
    override val tail: StrategicCompoundTailVertexSet<JunctionTailSelectionStrategy>,
    override val head: StrategicCompoundHeadVertexSet<JunctionHeadSelectionStrategy>,
) : HyperTransition

internal typealias ForkHeadTraversalStrategy = StaticSelectionStrategy<CompoundHeadVertexSet, OrthogonalCompoundHeadVertexSet>

internal class ForkHyperTransition(
    override val tail: TailVertexSet,
    override val head: StrategicCompoundHeadVertexSet<ForkHeadTraversalStrategy>,
) : HyperTransition

internal typealias JoinTailSelectionStrategy = AllOrNothingSelectionStrategy<CompoundTailVertexSet, EmptyTailVertexSet>

internal class JoinHyperTransition(
    override val tail: StrategicCompoundTailVertexSet<JoinTailSelectionStrategy>,
    override val head: SingletonHeadVertexSet,
) : HyperTransition