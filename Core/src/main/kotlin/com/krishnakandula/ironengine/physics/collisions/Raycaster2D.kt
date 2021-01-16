package com.krishnakandula.ironengine.physics.collisions

import org.joml.Vector2f

interface Raycaster2D {

    sealed class CastResult {

        class Collided(collisionCoordinates: Vector2f) : CastResult()

        object Missed : CastResult()
    }

    fun raycast(
        origin: Vector2f,
        direction: Vector2f,
        maxDistance: Float,
        interval: Float
    ) : CastResult
}