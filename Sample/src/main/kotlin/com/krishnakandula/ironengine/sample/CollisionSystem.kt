package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.physics.Line
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.physics.collisions.rayCastTriangle2D
import com.krishnakandula.ironengine.utils.clone
import com.krishnakandula.ironengine.utils.xyz
import org.joml.Vector3f
import org.joml.Vector4f

class CollisionSystem(private val spatialHash: SpatialHash2D) : System {

    private lateinit var componentManager: ComponentManager
    private val query: Archetype = Archetype(listOf(Transform.TYPE_ID))
    private val rotationSpeed: Float = 150f


    private var p0: Vector4f = Vector4f(-0.5f, -0.5f, 0.0f, 0f)
    private var p1: Vector4f = Vector4f(0.0f, 0.5f, 0.0f, 0f)
    private var p2: Vector4f = Vector4f(0.5f, -0.5f, 0.0f, 0f)
    private var line0: Vector4f = Vector4f(0f, 0.5f, 0f, 0f)
    private var line1: Vector4f = Vector4f(0f, 1.5f, 0f, 0f)

    private val collisionDistance: Float = 3f

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
//        sameDirectionRule(deltaTime)
        avoidCollisionsRule(deltaTime)
    }

    private fun avoidCollisionsRule(deltaTime: Double) {
        val cells: List<List<Entity>> = spatialHash.getCells()

        cells.stream().forEach { cell ->
            // for each boid's, raycast using the boid's position to check if it hits any other boid in it's cell
            for (i in 0..cell.lastIndex) {
                val boid: Entity = cell[i]
                val transform: Transform = componentManager.getComponent(boid) ?: continue

                line0.set(line0.mul(transform.model))
                line1.set(line1.mul(transform.model))

                val line = Line(transform.position.clone(), line1.sub(line0).xyz().mul(collisionDistance))

                for (x in 0..cell.lastIndex) {
                    if (x == i) {
                        continue
                    }

                    val otherBoid: Entity = cell[x]
                    val otherTransform: Transform = componentManager.getComponent(otherBoid) ?: continue

                    val transformedP0 = p0.mul(otherTransform.model).xyz()
                    val transformedP1 = p1.mul(otherTransform.model).xyz()
                    val transformedP2 = p2.mul(otherTransform.model).xyz()

                    val l0 = Line(transformedP0, transformedP1.clone().sub(transformedP0))
                    val l1 = Line(transformedP1, transformedP2.clone().sub(transformedP1))
                    val l2 = Line(transformedP2, transformedP0.clone().sub(transformedP2))

                    val collision: Vector3f? = rayCastTriangle2D(line, l0, l1, l2)
                    if (collision != null) {
                        println("Will collide")
                    }

                    // reset p0,p1,p2 vertices
                    p0.set(-0.5f, -0.5f, 0.0f, 0f)
                    p1.set(0.0f, 0.5f, 0.0f, 0f)
                    p2.set(0.5f, -0.5f, 0.0f, 0f)
                }
            }

            // reset line vertices
            line0.set(0f, 0.5f, 0f, 0f)
            line1.set(0f, 1.5f, 0f, 0f)
        }
    }

    private fun sameDirectionRule(deltaTime: Double) {
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
                transform.rotate(0f, 0f,  (rotateDirection * deltaTime * rotationSpeed).toFloat())
                transform.updateModel()
            }
        }
    }
}
