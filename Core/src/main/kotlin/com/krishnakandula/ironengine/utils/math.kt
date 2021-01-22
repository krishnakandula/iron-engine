package com.krishnakandula.ironengine.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

fun Vector3f.clone(): Vector3f = Vector3f(this)

fun Matrix4f.clone(): Matrix4f = Matrix4f(this)

fun Vector4f.xyz(): Vector3f = Vector3f(this.x, this.y, this.z)