package dev.timray.kotomata.model

internal sealed interface Hyperedge {
    val sourceSet: Set<OrNode>
    val targetSet: Set<OrNode>
}