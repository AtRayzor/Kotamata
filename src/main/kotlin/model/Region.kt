package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId
import kotlinx.coroutines.CoroutineScope

internal sealed interface RegionStatus {
    object Inactive : RegionStatus
    data class Active(
        val executionScope: CoroutineScope,
        val workScope: CoroutineScope,
    )
}

internal data class Region(
   override val id: String,
    var status: RegionStatus = RegionStatus.Inactive,
) : Atom {
    companion object {
        fun create() = Region(id = generateRandomId())
    }
}