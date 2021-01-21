package com.krishnakandula.ironengine.ecs.component

import com.krishnakandula.ironengine.utils.BitSet

class Archetype(componentTypeIds: List<Int>? = null,
                internal val componentsMask: BitSet = BitSet()) {

    init {
        if (componentTypeIds != null) {
            for (i in 0..componentTypeIds.lastIndex) {
                addComponent(componentTypeIds[i])
            }
        }
    }

    fun hasComponent(typeId: Int): Boolean = componentsMask.get(typeId)

    internal fun addComponent(component: Component) {
        addComponent(component.typeId)
    }

    internal fun addComponent(typeId: Int) {
        componentsMask.set(typeId)
    }

    internal fun removeComponent(typeId: Int) {
        componentsMask.clear(typeId)
    }


    internal fun isSubsetOf(required: Archetype): Boolean {
        return componentsMask.isSubsetOf(required.componentsMask)
    }

    internal fun clone(): Archetype = Archetype(componentsMask = componentsMask.clone())

    override fun hashCode(): Int = componentsMask.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false

        return when (other) {
            !is Archetype -> false
            else -> {
                return other.componentsMask == componentsMask
            }
        }
    }

}
