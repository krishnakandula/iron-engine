package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone

class MovementSystem(private val worldXBoundary: Float, private val worldYBoundary: Float) : System {

    private lateinit var componentManager: ComponentManager
    private val query: Archetype = Archetype(listOf(Transform.TYPE_ID, MovementComponent.TYPE_ID))

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        componentManager = scene.componentManager
    }

    override fun fixedUpdate(deltaTime: Double) {
        super.fixedUpdate(deltaTime)

        val components: List<Entity> = componentManager.query(query)
        components.stream().forEach { entity ->
            val transform = componentManager.getComponent<Transform>(entity) ?: return@forEach
            val movement = componentManager.getComponent<MovementComponent>(entity) ?: return@forEach

            val directionClone = transform.direction.clone()
            directionClone.set(transform.direction).mul(deltaTime.toFloat() * movement.speed)
            transform.translate(directionClone)

            // check if boid is past world boundary
            val position = transform.position
            if (position.x > worldXBoundary || position.x < -worldXBoundary) {
                position.x = -position.x
            } else if (position.y > worldYBoundary || position.y < -worldYBoundary) {
                position.y = -position.y
            }

            transform.updateModel()
        }
    }
}
