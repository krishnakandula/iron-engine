package com.krishnakandula.ironengine.physics

import com.krishnakandula.ironengine.ecs.component.Component
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Contains position, rotation, scale, and direction data for an entity.
 *
 * Transforms calculate a model matrix using relevant vectors and the transform's parent's
 * model matrix. When a transform is updated, all of its descendents will also be updated
 * recursively. This is not performed lazily; i.e. changing a transform's vector will
 * immediately trigger a recursive update so that all affected entities will have
 * up-to-date model matrices.
 */
class Transform(
    position: Vector3f = Vector3f(0f),
    rotation: Vector3f = Vector3f(0f),
    scale: Vector3f = Vector3f(1f),
    direction: Vector3f = Vector3f(0f, 1f, 0f),
    val model: Matrix4f = Matrix4f(),
    parent: Transform? = null,
    children: MutableSet<Transform> = mutableSetOf(),
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }

    var position: Vector3f = position
        set(value) {
            field = value
            updateModel()
        }

    var rotation: Vector3f = rotation
        set(value) {
            field = value
            updateModel()
        }

    var scale: Vector3f = scale
        set(value) {
            field = value
            updateModel()
        }

    var direction: Vector3f = direction
        set(value) {
            field = value
            updateModel()
        }

    var parent: Transform? = parent
        set(value) {
            if (value == null) {
                return
            }
            field = value
            updateModel()
        }

    var children: MutableSet<Transform> = children
        private set

    fun translate(translation: Vector3f) {
        position.add(translation)
        updateModel()
    }

    fun rotate(x: Float, y: Float, z: Float) {
        rotation.add(x, y, z)

        val roll: Float = rotation.z
        direction.x = cos(Math.toRadians(roll))
        direction.y = sin(Math.toRadians(roll))
        direction.z = 0f
        direction.normalize()

        updateModel()
    }

    fun setLookRotation(velocity: Vector3f) {
        // calculate roll based off of x and y
        val rollRadians: Float = atan2(velocity.y, velocity.x)
        val rollDegrees: Float = rollRadians * (180f / Math.PI.toFloat())

        rotation.z = rollDegrees

        updateModel()
    }

    fun scale(scale: Vector3f) {
        this.scale.mul(scale)
        updateModel()
    }

    fun addChild(child: Transform) {
        children.add(child)
    }

    fun removeChild(child: Transform) {
        children.remove(child)
    }

    private fun updateModel() {
        val roll: Float = rotation.z
        direction.x = cos(Math.toRadians(roll))
        direction.y = sin(Math.toRadians(roll))
        direction.z = 0f
        direction.normalize()

        // Reset model to the identity matrix
        model.identity()

        // If the entity has a parent, use the parent's model matrix
        // as the basis. The parent's matrix should already be updated.
        if (parent != null) {
            model.mul(parent!!.model)
        }

        // Apply transformation vectors to model matrix
        model
            .translate(position)
            .rotate(Math.toRadians(roll - 90f), 0f, 0f, 1f)
            .scale(scale)

        // Recursively update all child transforms
        if (children.isNotEmpty()) {
            children.forEach { it.updateModel() }
        }
    }
}
