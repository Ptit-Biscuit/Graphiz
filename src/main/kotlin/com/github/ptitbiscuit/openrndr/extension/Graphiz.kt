package com.github.ptitbiscuit.openrndr.extension

import com.github.ptitbiscuit.Graph
import com.github.ptitbiscuit.Vertex
import org.openrndr.Extension
import org.openrndr.MouseButton
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.draw.loadFont
import org.openrndr.math.Vector2
import org.openrndr.text.writer
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Graphiz<T>(private val graph: Graph<T>, fontUrl: String) : Extension {
    override var enabled = true

    private val font = loadFont(fontUrl, 16.0)
    private val radius = 25.0

    private var origin = Vector2.ZERO

    private var selected: Vertex<T>? = null
    private var verticesPosition = mutableMapOf<Vertex<T>, Vector2>()

    override fun setup(program: Program) {
        origin = Vector2(program.drawer.width * 0.5, program.drawer.height * 0.5)

        verticesPosition = graph.vertices.withIndex()
            .associate { (index, vertex) -> vertex to getVertexPosition(index) }
            .toMutableMap()

        program.mouse.buttonDown.listen { event ->
            if (!event.propagationCancelled && event.button == MouseButton.LEFT) {
                val position = event.position - origin

                selected = verticesPosition.entries.find { (_, value) ->
                    (position.x - value.x).pow(2) + (position.y - value.y).pow(2) < radius * radius
                }?.key
            }
        }

        program.mouse.dragged.listen { event ->
            if (!event.propagationCancelled && event.button == MouseButton.LEFT) {
                selected?.let { verticesPosition.merge(it, event.dragDisplacement) { old, new -> old + new } }
            }
        }
    }

    override fun beforeDraw(drawer: Drawer, program: Program) {
        drawer.run {
            fontMap = font
            translate(origin)
            drawEdges(this)
            drawVertices(this)
        }
    }

    private fun drawEdges(drawer: Drawer) =
        graph.edges.forEach { (from, to, value) ->
            val startPos = verticesPosition[from]!!
            val endPos = verticesPosition[to]!!

            drawer.isolated {
                stroke = ColorRGBa.GREEN
                lineSegment(startPos, endPos)
                if (selected == from) {
                    writer {
                        value?.toString()?.let {
                            val xOffset = if (startPos.x < endPos.x) radius * 1.1 else radius * -1.1 - textWidth(it)
                            val yOffset = if (startPos.y < endPos.y) radius * 1.1 else radius * -1.1
                            text(it, startPos + Vector2(xOffset, yOffset))
                        }
                    }
                }
            }
        }

    private fun drawVertices(drawer: Drawer) =
        graph.vertices.map { vertex ->
            val vertexPosition = verticesPosition[vertex]!!
            drawer.isolated {
                fill = if (selected == vertex) ColorRGBa.YELLOW else ColorRGBa.WHITE
                circle(vertexPosition, radius)
                fill = ColorRGBa.BLACK
                writer {
                    val textTranslation = Vector2(textWidth(vertex.id) * 0.5, -5.0)
                    text(vertex.value.toString(), vertexPosition - textTranslation)
                }
            }
        }

    private fun getVertexPosition(index: Int): Vector2 {
        val vertexIndex = ((360 / graph.vertices.size) * index) * 0.017453292519943295 // to radians
        return Vector2(cos(vertexIndex), sin(vertexIndex)) * 100.0
    }
}