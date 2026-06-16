package dev.timray.kotomata.builder

import dev.timray.kotomata.model.MutableXHiGraph
import dev.timray.kotomata.model.RegionAtom

sealed interface RegionBuilderScope<ST : Any, C> :
    RegionOrCompositeStateBuilderScope<ST, C>

internal class RegionBuilderScopeImpl<ST : Any, C>(
    val containingStateId: Any,
    internal val graph: MutableXHiGraph,
    impl: RegionOrCompositeStateBuilderScopeImpl<ST, C> = RegionOrCompositeStateBuilderScopeImpl(
        containingStateId = containingStateId,
       region =  RegionAtom.create(),

    )
) : RegionBuilderScope<ST, C>, RegionOrCompositeStateBuilderScope<ST, C> by impl