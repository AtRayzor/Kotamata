package dev.timray.kotomata.builder

import dev.timray.kotomata.model.TransitionAction

class JoinSourceBuilder<S : Any, C: Any> internal constructor(val source: S)
class JoinTargetBuilder<T : Any, C: Any>

sealed interface JoinBuilderScope<T : Any> {
    fun <S : Any, C : Any> addSource(
        region: RegionDeclaration<C>,
        source: S
    ): JoinSourceBuilder<S, C>

    fun <T : Any, C : Any> setTarget(
        region: RegionDeclaration<C>,
        target: T
    ): JoinTargetBuilder<T, C>

    infix fun <S: Any, C: Any> JoinSourceBuilder<S, C>.withAction(
        action: TransitionAction<C>
    )

    infix fun <T: Any, C: Any> JoinTargetBuilder<T, C>.withAction(
        action: TransitionAction<C>
    )
}

internal class JoinBuilderScopeImpl<T : Any>: JoinBuilderScope<T> {
    override fun <S : Any, C : Any> addSource(
        region: RegionDeclaration<C>,
        source: S
    ): JoinSourceBuilder<S, C> {
        TODO("Not yet implemented")
    }

    override fun <T : Any, C : Any> setTarget(
        region: RegionDeclaration<C>,
        target: T
    ): JoinTargetBuilder<T, C> {
        TODO("Not yet implemented")
    }

    override fun <S : Any, C : Any> JoinSourceBuilder<S, C>.withAction(
        action: TransitionAction<C>
    ) {
        TODO("Not yet implemented")
    }

    override fun <T : Any, C : Any> JoinTargetBuilder<T, C>.withAction(
        action: TransitionAction<C>
    ) {
        TODO("Not yet implemented")
    }
}