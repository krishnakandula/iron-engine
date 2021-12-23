package com.krishnakandula.ironengine.graphics.rendering

import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.VertexAttribute
import com.krishnakandula.ironengine.graphics.camera.Camera
import com.krishnakandula.ironengine.graphics.textures.Sprite
import com.krishnakandula.ironengine.graphics.textures.Texture
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import com.krishnakandula.ironengine.utils.times
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW

class SpriteBatchRenderer(
    private val shader: Shader,
    private val camera: Camera
) : System() {

    companion object {
        private const val MAX_SPRITES: Int = 5

        /*
           Rect coordinates

         0(-.5,.5)   1(.5,.5)
            0-------0
            |       |
            |       |
            |       |
            0-------0
         2(-.5,-.5)  3(.5,-.5)
         */
        private val rect: Array<Vector3f> = arrayOf(
            Vector3f(-0.5f, 0.5f, 0f),
            Vector3f(0.5f, 0.5f, 0f),
            Vector3f(-0.5f, -0.5f, 0f),
            Vector3f(0.5f, -0.5f, 0f),
        )
        private val rectIndices: IntArray = intArrayOf(
            0, 1, 3,
            0, 3, 2
        )
    }

    private val requiredComponents: Archetype = Archetype(
        listOf(
            Transform.TYPE_ID,
            Sprite.TYPE_ID
        )
    )

    private val mesh: Mesh
    private var verticesIndex: Int = 0
    private var indexCount = 0
    private var vertexCount = 0
    private var timesFlushed = 0
    private var lastTexture: Texture? = null
    private val vertexAttributes: Array<VertexAttribute> = arrayOf(
        // position
        VertexAttribute(
            index = 0,
            size = 3,
            type = GL_FLOAT,
            normalized = false,
            stride = 5 * Float.SIZE_BYTES,
            pointer = 0L * Float.SIZE_BYTES
        ),
        // texture coordinates
        VertexAttribute(
            index = 1,
            size = 2,
            type = GL_FLOAT,
            normalized = false,
            stride = 5 * Float.SIZE_BYTES,
            pointer = 3L * Float.SIZE_BYTES
        )
    )
    private val vertices: FloatArray = FloatArray(MAX_SPRITES * (4 * 5))
    private val indices: IntArray = IntArray(MAX_SPRITES * 6)

    init {
        mesh = Mesh(
            vertices, indices, *vertexAttributes
        )
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        timesFlushed = 0

        val entities: List<Entity> = componentManager?.query(requiredComponents) ?: return
        begin()
        for (entity in entities) {
            val sprite = componentManager?.getComponent<Sprite>(entity) ?: continue
            val transform = componentManager?.getComponent<Transform>(entity) ?: continue
            // TODO: Sort sprites by depth
            draw(sprite, transform)
        }
        end()
    }

    private fun begin() {
        shader.use()
        shader.setMat4("projection", camera.projection)
        shader.setMat4("view", camera.view)
    }

    private fun draw(sprite: Sprite, transform: Transform) {
        // pos (3) + texture coordinates(2)
        val v1 = rect[0].clone() * transform.model
        val v2 = rect[1].clone() * transform.model
        val v3 = rect[2].clone() * transform.model
        val v4 = rect[3].clone() * transform.model

        // Add data for each vertex
        addPos(v1)
        addTexCoord(sprite.x2, sprite.y1)
        addPos(v2)
        addTexCoord(sprite.x2, sprite.y2)
        addPos(v3)
        addTexCoord(sprite.x1, sprite.y1)
        addPos(v4)
        addTexCoord(sprite.x1, sprite.y2)

        /*
        Add indices to index buffer. vertexCount tracks which vertex the index should refer to.
        Each rect has 4 vertices, so we increment by 4 after adding all indices.
         */
        indices[indexCount++] = rectIndices[0] + vertexCount
        indices[indexCount++] = rectIndices[1] + vertexCount
        indices[indexCount++] = rectIndices[2] + vertexCount
        indices[indexCount++] = rectIndices[3] + vertexCount
        indices[indexCount++] = rectIndices[4] + vertexCount
        indices[indexCount++] = rectIndices[5] + vertexCount

        vertexCount += 4
    }

    private fun addPos(vec: Vector3f) {
        vertices[verticesIndex++] = vec.x
        vertices[verticesIndex++] = vec.y
        vertices[verticesIndex++] = 0f
    }

    private fun addTexCoord(x: Float, y: Float) {
        vertices[verticesIndex++] = x
        vertices[verticesIndex++] = y
    }

    private fun end() {
        flush()
        shader.detach()
        println("Times flushed: $timesFlushed")
    }

    private fun flush() {
        ++timesFlushed

        mesh.bind()
        mesh.update(vertices, indices, GL_STATIC_DRAW, *vertexAttributes)
        glDrawElements(GL_TRIANGLES, mesh.indexCount, GL_UNSIGNED_INT, 0)
        mesh.unbind()

        // reset
        lastTexture = null
        indexCount = 0
        verticesIndex = 0
        vertexCount = 0
    }
}
