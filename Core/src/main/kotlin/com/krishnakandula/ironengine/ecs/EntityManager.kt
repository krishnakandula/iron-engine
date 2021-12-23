package com.krishnakandula.ironengine.ecs

class EntityManager {

    private var entityId: Int = 0

    fun createEntity(): Entity = Entity(entityId++)

    fun createEntities(quantity: Int): Array<Entity> = Array(quantity) { createEntity() }
}
