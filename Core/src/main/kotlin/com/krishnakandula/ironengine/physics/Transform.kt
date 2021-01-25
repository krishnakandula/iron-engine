package com.krishnakandula.ironengine.physics

import com.krishnakandula.ironengine.ecs.component.Component
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Transform(
    var position: Vector3f = Vector3f(0f),
    var rotation: Vector3f = Vector3f(0f),
    var scale: Vector3f = Vector3f(1f),
    var direction: Vector3f = Vector3f(0f, 1f, 0f)
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }

    var model: Matrix4f = Matrix4f()

    init {
        updateModel()
    }

    fun translate(translation: Vector3f) {
        position.add(translation)
    }

    fun rotate(rotation: Vector3f) {
        this.rotation.add(rotation)
    }

    fun rotate(x: Float, y: Float, z: Float) {
        rotation.add(x, y, z)

        val roll: Float = rotation.z
        direction.x = cos(Math.toRadians(roll))
        direction.y = sin(Math.toRadians(roll))
        direction.z = 0f
        direction.normalize()
    }

    fun setLookRotation(velocity: Vector3f) {
        // calculate roll based off of x and y
        val rollRadians: Float = atan2(velocity.y, velocity.x)
        val rollDegrees: Float = rollRadians * (180f / Math.PI.toFloat())

        rotation.z = rollDegrees
    }

    fun scale(scale: Vector3f) {
        this.scale.mul(scale)
    }

    fun updateModel() {
        val roll: Float = rotation.z
        model.identity()
            .translate(position)
            .rotate(Math.toRadians(roll - 90f), 0f, 0f, 1f)
            .scale(scale)
    }
}
