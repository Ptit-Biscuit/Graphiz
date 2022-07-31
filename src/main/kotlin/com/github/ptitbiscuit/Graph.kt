package com.github.ptitbiscuit

data class Graph<T>(val vertices: Set<Vertex<T>>, val edges: Set<Edge<T>>) {
    constructor(graphBuilder: GraphBuilder<T>) : this(graphBuilder.vertices, graphBuilder.edges)
    constructor(graphBuilder: GraphBuilder<T>.() -> Unit) : this(GraphBuilder<T>().apply(graphBuilder))

    val order = vertices.size
    val size = edges.size
    val degree = vertices.maxOf { degree(it.value) }

    val directed = edges.all { edge -> edge.weight != null }
    val undirected = edges.any { edge -> edge.weight == null }

    fun adjacencyFor(vertex: T): Collection<Edge<T>> = edges.filter { it.from == Vertex(vertex) }

    fun degree(vertex: T): Int = edges.filter { it.from == Vertex(vertex) || it.to == Vertex(vertex) }.size

    fun adjacency(): Map<T, Collection<Edge<T>>> =
        vertices.fold(mapOf()) { acc, vertex -> acc + (vertex.value to adjacencyFor(vertex.value)) }

    fun children(root: T, adjacent: Map<T, Collection<Edge<T>>> = adjacency()): Collection<Edge<T>> =
        adjacent[root]?.fold(adjacent[root]!!) { acc, edge ->
            acc + children(edge.to.value, adjacent.mapValues { adj -> adj.value.filter { it.to.value != root } })
        }
        ?: listOf()

    fun shortestPath(from: T, to: T): Collection<Edge<T>> =
        children(from).firstOrNull { it.to.value == to }
            ?.let { listOf(it) + shortestPath(from, it.from.value) }
            ?: listOf()
}
