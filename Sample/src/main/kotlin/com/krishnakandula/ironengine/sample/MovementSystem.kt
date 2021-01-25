package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clamp
import com.krishnakandula.ironengine.utils.times
import com.krishnakandula.ironengine.utils.timesAssign
import org.joml.Vector3f

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
        components.parallelStream().forEach { entity ->
            val transform: Transform = componentManager.getComponent(entity) ?: return@forEach
            val movement: MovementComponent = componentManager.getComponent(entity) ?: return@forEach

            val velocity: Vector3f = movement.acceleration * deltaTime.toFloat()
            velocity.normalize(movement.maxSpeed)

            // set rotation based on velocity
            transform.setLookRotation(velocity)
            velocity *= deltaTime.toFloat()

            transform.translate(velocity)

            // check if boid is past world boundary
            val position = transform.position
            if (position.x > worldXBoundary || position.x < -worldXBoundary) {
                position.x = (-position.x).clamp(-worldXBoundary + 0.1f, worldXBoundary - 0.1f)

            } else if (position.y > worldYBoundary || position.y < -worldYBoundary) {
                position.y = (-position.y).clamp(-worldYBoundary + 0.1f, worldYBoundary - 0.1f)
            }

            transform.updateModel()
        }
    }
}
