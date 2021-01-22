package com.krishnakandula.ironengine.graphics

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.camera.Camera
import com.krishnakandula.ironengine.physics.Transform
import org.lwjgl.opengl.GL11C.GL_TRIANGLES
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glDrawElements

class RenderingSystem(private val camera: Camera,
                      private val shader: Shader) : System {

    private lateinit var componentManager: ComponentManager
    private val requiredComponents: Archetype = Archetype(listOf(
        Transform.TYPE_ID,
        Mesh.TYPE_ID
    ))

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

        for (i in 0..entities.lastIndex) {
            val entity = entities[i]

            val transform = componentManager.getComponent<Transform>(entity) ?: continue
            val mesh = componentManager.getComponent<Mesh>(entity) ?: continue

            transform.updateModel()
            mesh.bind()
            shader.setMat4("model", transform.model)
            glDrawElements(GL_TRIANGLES, mesh.indexCount, GL_UNSIGNED_INT, 0)
            mesh.unbind()
        }
    }
}