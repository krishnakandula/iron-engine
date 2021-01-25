package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.DebugRenderer
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import org.joml.Vector3f
import kotlin.math.atan2

class CollisionSystem(private val spatialHash: SpatialHash2D, private val debugRenderer: DebugRenderer) : System {

    private lateinit var componentManager: ComponentManager
    private val query: Archetype = Archetype(listOf(Transform.TYPE_ID, MovementComponent.TYPE_ID))

    private val collisionDistanceSquared: Float = 3f

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

        // apply rules
//        sameDirectionRule(deltaTime, 200f)
//        avoidCollisionsRule(deltaTime, entities, 200f)
        cohesionRule(entities, 2f)
    }

    private fun cohesionRule(entities: List<Entity>, multiplier: Float) {
        entities.parallelStream().forEach { entity ->
            val transform: Transform = componentManager.getComponent(entity) ?: return@forEach
            val movement: MovementComponent = componentManager.getComponent(entity) ?: return@forEach

            // get nearby boids
            val nearbyBoids: List<Entity> = spatialHash.getNearby(transform.position)
            if (nearbyBoids.size <= 1) {
                return@forEach
            }
            var averageXPos = 0f
            var averageYPos = 0f
            var boidsCounted = 0

            for (i in 0..nearbyBoids.lastIndex) {
                val otherBoid: Entity = nearbyBoids[i]
                if (otherBoid == entity) {
                    continue
                }

                val otherTransform: Transform = componentManager.getComponent(otherBoid) ?: continue

                // if the boid is close by, use its position when calculating the average flock position
                if (transform.position.distanceSquared(otherTransform.position) <= collisionDistanceSquared) {
                    averageXPos += otherTransform.position.x
                    averageYPos += otherTransform.position.y
                    ++boidsCounted
                }
            }

            if (boidsCounted == 0) {
                return@forEach
            }

            averageXPos /= boidsCounted
            averageYPos /= boidsCounted

            val turnForce: Vector3f = Vector3f(averageXPos, averageYPos, 0f)
                .sub(transform.position)
                .mul(multiplier)


            movement.acceleration.add(turnForce)
        }
    }

    private fun sameDirectionRule(deltaTime: Double, multiplier: Float) {
        val cells: List<List<Entity>> = spatialHash.getCells()
        cells.stream().forEach { cell ->
            if (cell.size <= 1) {
                return@forEach
            }

            // calculate average z rotation of all boids
            var totalRotation = 0f
            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                totalRotation += componentManager.getComponent<Transform>(boid)?.position?.z ?: 0f
            }
            val averageRotation: Float = (totalRotation / cell.size) % 360

            // now rotate each bird in the direction of its rotation
            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                val transform: Transform = componentManager.getComponent(boid) ?: continue

                val zRotation = transform.rotation.z % 360
                val rotateDirection: Float = if (zRotation >= averageRotation) -1f else 1f
                transform.rotate(0f, 0f, (rotateDirection * deltaTime * multiplier).toFloat())
                transform.updateModel()
            }
        }
    }

    private fun avoidCollisionsRule(deltaTime: Double, entities: List<Entity>, multiplier: Float) {
        entities.stream().forEach { entity ->
            val transform: Transform = componentManager.getComponent(entity) ?: return@forEach

            for (i in 0..entities.lastIndex) {
                val otherEntity: Entity = entities[i]
                if (otherEntity == entity) {
                    continue
                }

                val otherTransform: Transform = componentManager.getComponent(otherEntity) ?: return@forEach
                if (otherTransform.position.distanceSquared(transform.position) <= collisionDistanceSquared) {
                    transform.rotate(0f, 0f, 1f * deltaTime.toFloat() * multiplier)
                }
            }

            transform.updateModel()
        }
    }

    private fun steerTowards(deltaTime: Double, transform: Transform, targetDirection: Vector3f, multiplier: Float) {
        val transformDirection: Vector3f = transform.direction.clone().normalize()
        targetDirection.normalize()
        val angle = atan2(targetDirection.y - transformDirection.y, targetDirection.x - transformDirection.x)
        println(angle)
        transform.rotate(0f, 0f, angle * (180f / 3.1415f) * deltaTime.toFloat() * multiplier)
        transform.updateModel()
    }
}
