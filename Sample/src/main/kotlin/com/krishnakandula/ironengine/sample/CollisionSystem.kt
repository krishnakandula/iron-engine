package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.rendering.DebugRenderer
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clamp
import com.krishnakandula.ironengine.utils.clone
import com.krishnakandula.ironengine.utils.times
import org.joml.Vector3f

class CollisionSystem(
    private val spatialHash: SpatialHash2D,
    private val debugRenderer: DebugRenderer? = null
) : System {

    private lateinit var componentManager: ComponentManager
    private val query: Archetype = Archetype(listOf(Transform.TYPE_ID, MovementComponent.TYPE_ID))

    private val nearbyDistance: Float = 2.5f
    private val separationDistance: Float = .3f

    private val steerForce: Float = 5f

    private val cohesionWeight: Float = 1f
    private val alignmentWeight: Float = 1f
    private val separationWeight: Float = 2.8f

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        componentManager = scene.componentManager
    }

    override fun fixedUpdate(deltaTime: Double) {
        super.fixedUpdate(deltaTime)

        // for now, colliders will match the triangles' mesh
        val entities: List<Entity> = componentManager.query(query)

        // update spatial hash
        spatialHash.clear()
        for (i in 0..entities.lastIndex) {
            val entity: Entity = entities[i]
            val transform: Transform = componentManager.getComponent(entity) ?: continue
            spatialHash.updatePosition(entity, transform.position)
        }


        entities.parallelStream().forEach { entity ->
            val transform: Transform = componentManager.getComponent(entity) ?: return@forEach
            val movement: MovementComponent = componentManager.getComponent(entity) ?: return@forEach

            // reset acceleration
            movement.acceleration.zero()

            // get nearby boids
            val nearbyBoids: List<Entity> = spatialHash.getNearby(transform.position)

            var separation = Vector3f(0f)
            var alignment = Vector3f(0f)
            var cohesion = Vector3f(0f)

            var boidsCounted = 0

            for (i in 0..nearbyBoids.lastIndex) {
                val otherBoid: Entity = nearbyBoids[i]
                if (otherBoid == entity) {
                    continue
                }

                val otherTransform: Transform = componentManager.getComponent(otherBoid) ?: continue

                val distance = otherTransform.position.distanceSquared(transform.position)
                if (distance <= nearbyDistance) {

                    if (distance <= separationDistance) {
                        // calculate separation
                        val separationOffset: Vector3f = otherTransform.position.clone()
                            .sub(transform.position)
                            .normalize()
                            .mul(-1f)
                        separation.add(separationOffset)
                    }

                    // calculate cohesion
                    cohesion.add(otherTransform.position)

                    // calculate alignment
                    alignment.add(otherTransform.direction)

                    ++boidsCounted
                }
            }

            if (boidsCounted == 0) return@forEach

            separation = steerTowardsTarget(separation, movement.velocity, movement.maxSpeed, steerForce)
            separation *= separationWeight

            alignment = steerTowardsTarget(alignment, movement.velocity, movement.maxSpeed, steerForce)
            alignment *= alignmentWeight

            cohesion.div(boidsCounted.toFloat())
            cohesion = steerTowardsTarget(cohesion.sub(transform.position), movement.velocity, movement.maxSpeed, steerForce)
            cohesion *= cohesionWeight

            movement.acceleration.add(separation)
            movement.acceleration.add(alignment)
            movement.acceleration.add(cohesion)
        }
    }

    private fun steerTowardsTarget(target: Vector3f, velocity: Vector3f, maxSpeed: Float, steerForce: Float): Vector3f {
        if (target.lengthSquared() == 0f) return target
        val desiredVelocity: Vector3f = target.normalize().mul(maxSpeed)
        return desiredVelocity.sub(velocity).clamp(0.1f, steerForce)
    }
}
