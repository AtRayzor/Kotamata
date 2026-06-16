package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId

internal data class RegionNode(
    override val payload: RegionAtom,
    override val id: Any = generateRandomId(),
): AndNode

internal fun RegionAtom.createNode() = RegionNode(this)