package graphiz

import org.openrndr.application

fun main() {
    val graph = Graph {
        val aud = Vertex("AUD")
        val chf = Vertex("CHF")
        val eur = Vertex("EUR")
        val inr = Vertex("INR")
        val jpy = Vertex("JPY")
        val usd = Vertex("USD")
        val krw = Vertex("KRW")

        vertices { setOf(aud, chf, eur, inr, jpy, usd, krw) }

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

        inverseEdges { edges ->
            edges
                .filter { !edges.contains(Edge(it.to, it.from, null)) }
                .map { Edge(it.to, it.from, it.weight?.let { value -> "%.4f".format(1 / value.toString().toDouble()) }) }
                .toSet()
        }
    }

    application {
        program {
            extend(Graphiz(graph, "src/main/resources/Roboto-Light.ttf"))
        }
    }
}