package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.graphics.DebugRenderer
import org.joml.Vector3f

class SpatialHash2D(
    private val worldWidth: Float,
    private val worldHeight: Float,
    private val rows: Int,
    private val cols: Int,
    private val debugRenderer: DebugRenderer? = null
) : System {

    var drawHash: Boolean = false

    private val spatialHash: List<MutableList<Entity>>
    val cellWidth = worldWidth / cols
    val cellHeight = worldHeight / rows

    init {
        val totalCells = cols * rows
        spatialHash = List(totalCells) { mutableListOf() }
    }

    override fun update(deltaTime: Double) {
        if (!drawHash) {
            return
        }

        // Draw horizontal lines
        var y: Float = -worldHeight / 2f
        var x: Float = worldWidth / 2f
        for (line in 0 until rows) {
            debugRenderer?.drawLine(Vector3f(-x, y, 0f), Vector3f(x, y, 0f))
            y += cellHeight
        }

        y = worldHeight / 2f
        x = -worldWidth / 2f
        for (line in 0 until cols) {
            debugRenderer?.drawLine(Vector3f(x, -y, 0f), Vector3f(x, y, 0f))
            x += cellWidth
        }
    }

    fun clear() {
        for (i in 0..spatialHash.lastIndex) {
            spatialHash[i].clear()
        }
    }

    fun updatePosition(entity: Entity, position: Vector3f) {
        val row = getRow(position)
        val col = getCol(position)
        if (row < 0 || col < 0 || row >= rows || col >= cols) {
            return
        }
        val cell = getCellIndex(getRow(position), getCol(position))
        if (cell >= spatialHash.size) {
            println("cell: $cell entity: $entity")
        } else {
            spatialHash[cell].add(entity)
        }
    }

    /**
     * Gets entities in same cell, and 8 adjacent cells
     */
    fun getNearby(position: Vector3f): List<Entity> {
        val row: Int = getRow(position)
        val col: Int = getCol(position)

        if (row >= rows || row < 0 || col >= cols || col < 0) {
            return emptyList()
        }

        val nearbyEntities: MutableList<Entity> = mutableListOf()
        nearbyEntities.addAll(spatialHash[getCellIndex(row, col)])
        if (row - 1 >= 0) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row - 1, col)])
        }
        if (row + 1 < rows) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row + 1, col)])
        }
        if (col - 1 >= 0) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row, col - 1)])
        }
        if (col + 1 < cols) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row, col + 1)])
        }
        if (col + 1 < cols && row + 1 < rows) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row + 1, col + 1)])
        }
        if (col - 1 >= 0 && row - 1 >= 0) {
            nearbyEntities.addAll(spatialHash[getCellIndex(row - 1, col - 1)])
        }

        return nearbyEntities
    }

    fun getCells(): List<List<Entity>> = spatialHash

    /**
     * @param position position of the entity
     * @return the spatial cell for the position vector
     */
    private fun getCellIndex(row: Int, col: Int): Int = (row * cols) + col

    private fun getRow(position: Vector3f): Int {
        val y: Float = position.y + (worldHeight / 2f)
        return (y / cellHeight).toInt()
    }

    private fun getCol(position: Vector3f): Int {
        val x: Float = position.x + (worldWidth / 2f)
        return (x / cellWidth).toInt()
    }
}
