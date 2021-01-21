package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.component.Component

class MovementComponent(val speed: Float = 5f) : Component(TYPE_ID) {
    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }
}
