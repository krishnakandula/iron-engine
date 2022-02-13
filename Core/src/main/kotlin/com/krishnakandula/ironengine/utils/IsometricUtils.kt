package com.krishnakandula.ironengine.utils

import org.joml.Matrix3f
import org.joml.Vector3f

val CARTESIAN_TO_ISOMETRIC_PROJECTION: Matrix3f = Matrix3f(
    1f, 1f, 0f,
    -0.5f, 0.5f, 0f,
    0f, 0f, 0f
)

val ISOMETRIC_TO_CARTESIAN_PROJECTION: Matrix3f = Matrix3f(
    0.5f, -1f, 0f,
    0.5f, 1f, 0f,
    0f, 0f, 0f
)

fun Vector3f.toIsometric(): IsometricVector3f {
    val res = this * CARTESIAN_TO_ISOMETRIC_PROJECTION
    return IsometricVector3f(res.x, res.y, res.z)
}

class IsometricVector3f(x: Float, y: Float, z: Float) {

    val x: Float
        get() = isoCoords.x

    val y: Float
        get() = isoCoords.y

    val isoCoords: Vector3f = Vector3f(x, y, z)

    fun toCartesianVector3f(): Vector3f {
        return isoCoords * ISOMETRIC_TO_CARTESIAN_PROJECTION
    }
}
