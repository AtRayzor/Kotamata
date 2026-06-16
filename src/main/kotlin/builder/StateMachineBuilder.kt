package dev.timray.kotomata.builder

import dev.timray.kotomata.model.RegionAtom
import dev.timray.kotomata.utils.generateRandomId

class StateMachineBuilderScope<S : Any, C> internal constructor() :
    RegionOrCompositeStateBuilderScope<S, C> by RegionOrCompositeStateBuilderScopeImpl(
        containingStateId = generateRandomId(),
        region = RegionAtom.create(),
    )


