package dev.timray.kotomata.model

internal sealed interface CompoundSelectionStrategy

internal data class DynamicSelectionStrategy<O: SingletonVertexSet>(
    val selector: (Configuration<Any?>) -> O
): CompoundSelectionStrategy

internal data class StaticSelectionStrategy<I, O>(
    val selector: (Configuration<Any?>, I) -> O
): CompoundSelectionStrategy
    where I: VertexSet, O: VertexSet

internal  class AllOrNothingSelectionStrategy<S, OE>(
   private val allowed: (Configuration<Any?>, S) -> Boolean,
   private val empty: OE
): CompoundSelectionStrategy

where S: VertexSet, OE : EmptyVertexSet
{
    fun selector(config: Configuration<Any?>, set: S): VertexSet {
        return if(allowed(config, set)) set else empty
    }
}