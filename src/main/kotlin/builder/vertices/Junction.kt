package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.model.MutableXHiGraph
import dev.timray.kotomata.model.RegionAtom


internal data class JunctionDeclaration<S : Any, Src : Any, C, T : Any>(
    val id: S,
    val source: Src,
    val containingStateId: Any,
    val containingRegion: RegionAtom,
    val selector: ChoiceSelector<C>
)

private fun MutableXHiGraph.buildJunctionHyperTransition(

) {

}