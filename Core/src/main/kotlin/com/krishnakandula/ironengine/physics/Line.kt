package com.krishnakandula.ironengine.physics

import com.krishnakandula.ironengine.utils.clone
import org.joml.Vector3f

data class Line(val origin: Vector3f, val direction: Vector3f) {

    fun closestPoint(point: Vector3f): Vector3f {
        val wSegment = point.clone().sub(origin)
        val projectionLength = wSegment.dot(direction) / direction.length()
        val projection = origin.clone().normalize().mul(projectionLength)

        return projection.add(origin)
    }
}

data class Ray(val origin: Vector3f, val direction: Vector3f)
