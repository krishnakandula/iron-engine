package com.krishnakandula.ironengine.graphics.rendering

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.textures.Texture
import com.krishnakandula.ironengine.physics.Transform

class SpriteBatchRenderer(private val shader: Shader) : System {

    companion object {
        private const val MAX_SPRITES: Int = 256
    }

    private lateinit var componentManager: ComponentManager
    private val requiredComponents: Archetype = Archetype(
        listOf(
            Transform.TYPE_ID,
            Texture.TYPE_ID
        )
    )

    private val vertices: FloatArray = FloatArray(MAX_SPRITES * 6)
    private var vertexIndex: Int = 0
    private var lastTexture: Texture? = null

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.componentManager = scene.componentManager
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)

        val entities: List<Entity> = componentManager.query(requiredComponents)
        begin()
        for (entity in entities) {
            val texture = componentManager.getComponent<Texture>(entity) ?: continue
            val transform = componentManager.getComponent<Transform>(entity) ?: continue

            draw(texture, transform)
        }
        end()
    }

    private fun begin() {
        shader.use()
    }

    private fun draw(texture: Texture, transform: Transform) {
        if (vertexIndex + 20 > vertices.size) {
            flush()
        } else if (lastTexture != texture) {
            flush()
            lastTexture = texture
        }

        // pos (3) + texture coordinates(2)
        val x1: Float = transform.

    }

    private fun end() {
        shader.detach()
    }

    private fun flush() {
        // TODO: Draw vertices
        lastTexture = null
    }
}