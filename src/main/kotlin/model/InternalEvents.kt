package dev.timray.kotomata.model

internal sealed interface InternalEvent: Event

internal object StartEvent: InternalEvent
internal object InitialEvent: InternalEvent
internal object CompletionEvent: InternalEvent
internal object JoinEvent: InternalEvent
internal object FinalEvent: InternalEvent