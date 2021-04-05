package com.krishnakandula.ironengine.physics

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.component.Component

class Parent(val entity: Entity) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }
}