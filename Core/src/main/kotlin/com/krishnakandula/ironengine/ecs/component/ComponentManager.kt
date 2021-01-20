package com.krishnakandula.ironengine.ecs.component

import com.krishnakandula.ironengine.ecs.Entity

class ComponentManager {

    private val archetypes: MutableMap<Archetype, MutableSet<Entity>> = mutableMapOf()

    private val components: MutableMap<Class<out Component>, ComponentMapper<out Component>> = mutableMapOf()

    inline fun < reified T : Component> addComponent(entity: Entity, component: T) {
        addComponent(entity, component, T::class.java)
    }

    fun <T : Component> addComponent(entity: Entity, component: T, clazz: Class<T>) {
        // remove entity from previous archetype
        archetypes[entity.archetype]?.remove(entity)
        // add entity to new archetype
        entity.archetype.addComponent(component)
        archetypes.computeIfAbsent(entity.archetype) { mutableSetOf() }.add(entity)
        // add component to component mapper
        val mapper = components.computeIfAbsent(clazz) { ComponentMapper<T>() } as ComponentMapper<T>
        mapper.addComponent(entity, component)
    }

    inline fun <reified T : Component> removeComponent(entity: Entity, typeId: Int) {
        removeComponent(entity, typeId, T::class.java)
    }

    fun <T : Component> removeComponent(entity: Entity, typeId: Int, clazz: Class<T>) {
        // check if entity has component
        if (!entity.archetype.hasComponent(typeId)) {
            return
        }

        archetypes[entity.archetype]?.remove(entity)
        entity.archetype.removeComponent(typeId)
        archetypes.computeIfAbsent(entity.archetype) { mutableSetOf() }.add(entity)
        components[clazz]?.removeComponent(entity)
    }

    inline fun <reified T : Component> getComponent(entity: Entity): T? = getComponent(entity, T::class.java)

    fun <T : Component> getComponent(entity: Entity, clazz: Class<T>): T? {
        val componentMapper = components[clazz]
        val component = componentMapper?.getComponent(entity) ?: return null

        return component as T
    }

    fun query(requiredComponents: Archetype): List<Entity> {
        val entities = mutableListOf<Entity>()
        archetypes.forEach { entry ->
            val archetype = entry.key
            if (requiredComponents.isSubsetOf(archetype)) {
                entities.addAll(entry.value)
            }
        }

        return entities
    }
}
