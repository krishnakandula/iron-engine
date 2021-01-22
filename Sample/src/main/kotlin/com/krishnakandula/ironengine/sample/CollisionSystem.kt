package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import org.joml.Vector3f

class CollisionSystem(private val spatialHash: SpatialHash2D) : System {

    private lateinit var componentManager: ComponentManager
    private val query: Archetype = Archetype(listOf(Transform.TYPE_ID))

    private val collisionDistanceSquared: Float = 1f

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
        sameDirectionRule(deltaTime, 100f)
        avoidCollisionsRule(deltaTime, 200f)
        flockCenterRule(deltaTime, 700f)
    }

    private fun avoidCollisionsRule(deltaTime: Double, multiplier: Float) {
        val cells: List<List<Entity>> = spatialHash.getCells()

        cells.stream().forEach { cell ->
            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                val transform: Transform = componentManager.getComponent(boid) ?: continue

                for (x in 0..cell.lastIndex) {
                    if (x == i) {
                        continue
                    }

                    val otherBoid: Entity = cell[x]
                    val otherTransform: Transform = componentManager.getComponent(otherBoid) ?: continue

                    // get distance between boids
                    val distance: Vector3f = otherTransform.position.clone().sub(transform.position)
                    val distanceLength: Float = distance.lengthSquared()
                    if (distanceLength <= collisionDistanceSquared) {
                        // move in the opposite direction of the distance vector
                        steerTowards(deltaTime, otherTransform, distance, multiplier)
                        steerTowards(deltaTime, transform, distance.mul(-1f), multiplier)

                        otherTransform.updateModel()
                    }
                }
                transform.updateModel()
            }
        }
    }

    private fun sameDirectionRule(deltaTime: Double, multiplier: Float) {
        val cells: List<List<Entity>> = spatialHash.getCells()
        cells.parallelStream().forEach { cell ->
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

    private fun flockCenterRule(deltaTime: Double, multiplier: Float) {
        val cells: List<List<Entity>> = spatialHash.getCells()
        cells.parallelStream().forEach { cell ->
            if (cell.size <= 1) {
                return@forEach
            }

            var averageXPos = 0f
            var averageYPos = 0f
            var totalBoids = 0

            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                val transform: Transform = componentManager.getComponent(boid) ?: continue

                averageXPos += transform.position.x
                averageYPos += transform.position.y

                ++totalBoids
            }

            averageXPos /= totalBoids
            averageYPos /= totalBoids

            val flockCenter = Vector3f(averageXPos, averageYPos, 0f)

            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                val transform: Transform = componentManager.getComponent(boid) ?: continue

                val steerDirection: Vector3f = flockCenter.clone().sub(transform.position)
                steerTowards(deltaTime, transform, steerDirection, multiplier)
                transform.updateModel()
            }
        }
    }

    private fun steerTowards(deltaTime: Double, transform: Transform, targetDirection: Vector3f, multiplier: Float) {
        val transformDirection: Vector3f = transform.direction.clone().normalize()

        // dot product of normalized transform direction and steer direction gives rotation angle
        val rotationAngle = targetDirection.clone().normalize().dot(transformDirection)
        transform.rotate(0f, 0f, rotationAngle * deltaTime.toFloat() * multiplier)
    }
}
