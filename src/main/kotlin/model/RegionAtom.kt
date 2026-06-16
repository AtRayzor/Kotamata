package dev.timray.kotomata.model

import dev.timray.kotomata.utils.generateRandomId
import kotlinx.coroutines.CoroutineScope

sealed interface Region {
    val id: String
}

internal sealed interface RegionStatus {
    object Inactive : RegionStatus

    data class Active(
        val executionScope: CoroutineScope,
        val workScope: CoroutineScope,
        val context: Any?,
    ): RegionStatus
}

internal data class RegionAtom(
   override val id: String,
    var status: RegionStatus = RegionStatus.Inactive,
) : Atom, Region {
    companion object {
        fun create() = RegionAtom(id = generateRandomId())
    }
}

internal fun XHiGraph.getContextMap(): Map<String, Any?> {
    return getAllAndNodes()
        .map { rNode -> rNode.payload  }
        .filter { region -> region.status is RegionStatus.Active}
        .associate { region -> region.id to (region.status as RegionStatus.Active).context }
}