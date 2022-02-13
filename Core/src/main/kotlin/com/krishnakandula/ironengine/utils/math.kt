package com.krishnakandula.ironengine.utils

import org.joml.Matrix3f
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

operator fun Vector3f.times(mat: Matrix3f): Vector3f {
    val x = (this[0] * mat.m00()) + (this[1] * mat.m01()) + (this[2] * mat.m02())
    val y = (this[0] * mat.m10()) + (this[1] * mat.m11()) + (this[2] * mat.m12())
    val z = (this[0] * mat.m20()) + (this[1] * mat.m21()) + (this[2] * mat.m22())

    return Vector3f(x, y, z)
}

operator fun Vector3f.times(mat: Matrix4f): Vector3f {
    val x = (this[0] * mat.m00()) + (this[1] * mat.m10()) + (this[2] * mat.m20()) + (1.0f * mat.m30())
    val y = (this[0] * mat.m01()) + (this[1] * mat.m11()) + (this[2] * mat.m21()) + (1.0f * mat.m31())
    val z = (this[0] * mat.m02()) + (this[1] * mat.m12()) + (this[2] * mat.m22()) + (1.0f * mat.m32())

    return Vector3f(x, y, z)
}

operator fun Vector3f.times(vec: Vector3f): Vector3f = Vector3f(this.x * vec.x, this.y * vec.y, this.z * vec.z)

operator fun Vector3f.times(scalar: Float): Vector3f {
    val result: Vector3f = this.clone()
    result *= scalar

    return result
}

operator fun Vector3f.times(scalar: Double): Vector3f {
    val result: Vector3f = this.clone()
    result *= (scalar.toFloat())

    return result
}

operator fun Vector3f.timesAssign(scalar: Float) {
    this.x *= scalar
    this.y *= scalar
    this.z *= scalar
}
