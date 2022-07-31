package com.github.ptitbiscuit

data class Edge<T>(val from: Vertex<T>, val to: Vertex<T>, val weight: Any?) {
    infix fun <S : Any> value(that: S) = Edge(from, to, that)
}