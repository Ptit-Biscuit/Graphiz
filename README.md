# Graphiz [![](https://jitpack.io/v/Ptit-Biscuit/graphiz.svg)](https://jitpack.io/#Ptit-Biscuit/graphiz)
Graph manipulation and visualization using kotlin and openrndr.

## How to use
``` kotlin
val graph = Graph {
    // declare vertices
    val aud = Vertex("AUD")
    val chf = Vertex("CHF")
    val eur = Vertex("EUR")
    val inr = Vertex("INR")
    val jpy = Vertex("JPY")
    val usd = Vertex("USD")
    val krw = Vertex("KRW")

    vertices { setOf(aud, chf, eur, inr, jpy, usd, krw) }

    // create edges between vertices (weight is optional)
    edges {
        setOf(
            aud edgeWith chf value 0.9661,
            aud edgeWith jpy value 86.0305,
            eur edgeWith chf value "1.2053",
            eur edgeWith usd value 1.2989,
            inr edgeWith jpy,
            jpy edgeWith inr value 0.6571,
            jpy edgeWith krw value 13.1151,
        )
    }

    // Create inverse edges if needed
    inverseEdges { edges ->
        edges
            .filter { !edges.contains(Edge(it.to, it.from, null)) }
            .map { Edge(it.to, it.from, it.weight?.let { value -> "%.4f".format(1 / value.toString().toDouble()) }) }
            .toSet()
    }
}
```

## Features
- [x] Create a generic graph
- [x] Inverse specific edges' weight
- [x] Optional weights on edges
- [x] Basic order, size, degree of graph
- [x] Directed or undirected graph
- [x] Vertex degree, adjacency, children
- [x] Shortest path between two vertices

## Visualisation
You can visualise graphs with the provided openrndr extension.

``` kotlin
application {
    program {
        val fontUrl = "src/main/resources/Roboto-Light.ttf"
        extend(Graphiz(graph, fontUrl))
    }
}
```

![example.png](example.png)