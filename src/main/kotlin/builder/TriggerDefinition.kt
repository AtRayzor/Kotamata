package dev.timray.kotomata.builder

import dev.timray.kotomata.model.Event
import kotlin.reflect.KClass

internal data class TriggerDefinition(
    val target: Any,
    val eventClass: KClass<Event>,
)