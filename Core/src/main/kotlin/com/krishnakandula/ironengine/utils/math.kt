package com.krishnakandula.ironengine.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

fun Vector3f.clone(): Vector3f = Vector3f(this)

fun Matrix4f.clone(): Matrix4f = Matrix4f(this)

fun Vector4f.xyz(): Vector3f = Vector3f(this.x, this.y, this.z)

fun Float.clamp(min: Float, max: Float): Float = this.coerceAtMost(max).coerceAtLeast(min)

fun Vector3f.clamp(min: Float, max: Float): Vector3f {
    if (max == 0f) {
        this.zero()
        return this
    }

    val lengthSquared = this.lengthSquared()
    val minSquared = min * min
    val maxSquared = max * max

    if (lengthSquared > maxSquared) {
        this.normalize(max)
    }
    if (lengthSquared < minSquared && min != 0f) {
        this.normalize(min)
    }

    return this
}

operator fun Vector3f.times(scalar: Float): Vector3f {
    val result: Vector3f = this.clone()
    result *= scalar

    return result
}

operator fun Vector3f.timesAssign(scalar: Float) {
    this.x *= scalar
    this.y *= scalar
    this.z *= scalar
}
