package com.github.ptitbiscuit

class GraphBuilder<T> internal constructor() {
    internal var vertices = setOf<Vertex<T>>()
    internal var edges = setOf<Edge<T>>()

    fun vertices(init: () -> Set<Vertex<T>>) {
        vertices = init()
    }

    fun edges(init: () -> Set<Edge<T>>) {
        edges = init()
    }

    fun inverseEdges(edgesToInvert: Set<Edge<T>>? = null, inverse: (Set<Edge<T>>) -> Set<Edge<T>>) {
        edges = edges + (edgesToInvert?.let { inverse(it) } ?: inverse(edges))
    }
}