package graphiz

data class Vertex<T>(val value: T, val id: String = value.toString()) {
    infix fun edgeWith(that: Vertex<T>) = Edge(this, that, null)
}