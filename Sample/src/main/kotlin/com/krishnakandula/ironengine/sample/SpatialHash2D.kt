package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import org.joml.Vector3f
import kotlin.math.abs

class SpatialHash2D(
    private val worldWidth: Float,
    private val worldHeight: Float,
    rows: Int,
    private val cols: Int
) {

    private val spatialHash: List<MutableList<Entity>>
    private val cellWidth = worldWidth / cols
    private val cellHeight = worldHeight / rows

    init {
        val totalCells = cols * rows
        spatialHash = List(totalCells) { mutableListOf() }
    }

    fun clear() {
        for (i in 0..spatialHash.lastIndex) {
            spatialHash[i].clear()
        }
    }

    fun updatePosition(entity: Entity, position: Vector3f) {
        if (abs(position.y) >= worldHeight / 2f || abs(position.x) >= worldHeight / 2f) {
            return
        }
        val cell = getCell(position)
        if (cell >= spatialHash.size) {
            println(cell)
        }
        spatialHash[cell].add(entity)
    }

    fun getNearby(position: Vector3f): List<Entity> {
        if (abs(position.y) >= worldHeight / 2f || abs(position.x) >= worldHeight / 2f) {
            return emptyList()
        }
        return spatialHash[getCell(position)]
    }

    fun getCells(): List<List<Entity>> = spatialHash

    /**
     * @param position position of the entity
     * @return the spatial cell for the position vector
     */
    private fun getCell(position: Vector3f): Int {
        val x: Float = position.x + (worldWidth / 2f)
        val y: Float = position.y + (worldHeight / 2f)

        val row: Int = (y / cellHeight).toInt()
        val col: Int = (x / cellWidth).toInt()

        return (row * cols) + col
    }
}
