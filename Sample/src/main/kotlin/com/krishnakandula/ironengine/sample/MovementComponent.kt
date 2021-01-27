package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.component.Component
import org.joml.Vector3f

class MovementComponent(val acceleration: Vector3f = Vector3f(0f, 0f, 0f),
                        val velocity: Vector3f = Vector3f(1f, 1f, 0f),
                        val minSpeed: Float = 3.5f,
                        val maxSpeed: Float = 4f) : Component(TYPE_ID) {
    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }
}
