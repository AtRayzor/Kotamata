package dev.timray.kotomata.builder

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

sealed interface RegionDeclaration<C: Any> {
    val id: String
}

internal data class RegionDeclarationImpl<C: Any>(
    override val id: String,
    val contextClass: KClass<C>,
    val coroutineDispatcher: CoroutineDispatcher,
): RegionDeclaration<C>