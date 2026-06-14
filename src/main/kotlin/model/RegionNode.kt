package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId

internal data class RegionNode(
    override val payload: Region,
    override val id: Any = generateRandomId(),
): AndNode

internal fun Region.createNode() = RegionNode(this)