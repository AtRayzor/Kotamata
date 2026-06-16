package dev.timray.kotomata.builder.vertices

import dev.timray.kotomata.builder.BuilderContext
import dev.timray.kotomata.model.PseudoState


internal typealias PseudoStateFactory = BuilderContext.() -> PseudoState<Any>