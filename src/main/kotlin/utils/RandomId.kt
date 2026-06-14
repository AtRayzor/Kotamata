package dev.timray.kotomata.utils

import kotlin.io.encoding.Base64
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun generateRandomId(): String {
    val bytes = Uuid.random().toByteArray()

    return Base64.encode(bytes).filter { it !in "=/" }
}