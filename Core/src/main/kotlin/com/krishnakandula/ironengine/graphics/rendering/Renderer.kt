package com.krishnakandula.ironengine.graphics.rendering

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.Camera
import com.krishnakandula.ironengine.physics.Transform
import org.lwjgl.opengl.GL11C.GL_TRIANGLES
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glDrawElements

class Renderer(
    private val camera: Camera,
    private val shader: Shader
) : System {

    private lateinit var componentManager: ComponentManager
    private val requiredComponents: Archetype = Archetype(
        listOf(
            Transform.TYPE_ID,
            Mesh.TYPE_ID
        )
    )

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)

        this.componentManager = scene.componentManager
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        val entities: List<Entity> = componentManager.query(requiredComponents)

        shader.use()
        shader.setMat4("projection", camera.projection)
        shader.setMat4("view", camera.view)

        entities
            // First, find all entities that have a root transform (i.e. an entity whose transform
            // component doesn't have a parent).
            .filter(this::entityWithNoParentTransform)
            // Now, recursively render the entity and all of it's children
            .forEach(this::renderEntity)
    }

    private fun entityWithNoParentTransform(entity: Entity): Boolean {
        val transform = componentManager.getComponent<Transform>(entity) ?: return false
        return transform.parent == null
    }

    private fun renderEntity(entity: Entity) {
        val transform = componentManager.getComponent<Transform>(entity) ?: return
        val mesh = componentManager.getComponent<Mesh>(entity) ?: return

        mesh.bind()
        shader.setMat4("model", transform.model)
        glDrawElements(GL_TRIANGLES, mesh.indexCount, GL_UNSIGNED_INT, 0)
        mesh.unbind()
    }
}
