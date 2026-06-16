package dev.timray.kotomata.model

sealed interface Vertex<out S: Any>: Atom {
  override  val id: S
}
