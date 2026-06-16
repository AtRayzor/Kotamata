package dev.timray.kotomata.builder

import dev.timray.kotomata.model.CompositeStateVertex
import dev.timray.kotomata.model.MutableXHiGraph
import dev.timray.kotomata.model.RegionAtom


internal interface BuilderContext{
    val graph: MutableXHiGraph
    val region: RegionAtom
    val containingState: CompositeStateVertex<Any, Any?>
}

internal data class BuilderContextImpl(
    override val graph: MutableXHiGraph,
    override val region: RegionAtom,
    override val containingState: CompositeStateVertex<Any, Any?>,
): BuilderContext