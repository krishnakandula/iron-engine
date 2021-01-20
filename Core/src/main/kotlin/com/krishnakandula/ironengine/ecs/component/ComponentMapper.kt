package com.krishnakandula.ironengine.ecs.component

import com.krishnakandula.ironengine.ecs.Entity

class ComponentMapper<T : Component> {

    private val entityToComponent: MutableMap<Entity, T> = mutableMapOf()

    fun getComponent(entity: Entity): T? = entityToComponent[entity]

    fun addComponent(entity: Entity, component: T) {
        entityToComponent[entity] = component
    }

    fun removeComponent(entity: Entity) {
        entityToComponent.remove(entity)
    }
}
