package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.ecs.component.Archetype

class Entity internal constructor(val id: Int) {

    val archetype: Archetype = Archetype()

    override fun hashCode(): Int = id

    override fun equals(other: Any?): Boolean {
        if (other == null) return false

        return when (other) {
            is Entity -> other.id == id
            else -> false
        }
    }
}
