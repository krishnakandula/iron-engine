package com.krishnakandula.ironengine.graphics

import com.krishnakandula.ironengine.ecs.component.Component
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_FLOAT
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glEnableVertexAttribArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL30.glVertexAttribPointer

class Mesh(
    vertices: FloatArray,
    indices: IntArray,
    vecSize: Int,
    stride: Int
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }

    val vao: Int
    val vertexCount: Int
    val indexCount: Int

    private val vbo: Int
    private val ebo: Int

    init {
        vao = glGenVertexArrays()
        vbo = glGenBuffers()
        ebo = glGenBuffers()

        vertexCount = vertices.size
        indexCount = indices.size

        // bind VAO
        glBindVertexArray(vao)

        // bind VBO
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, vecSize, GL_FLOAT, false, stride * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)

        // bind EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // unbind VBO but not VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        // unbind VAOS
        glBindVertexArray(0)
    }

    fun bind() {
        glBindVertexArray(vao)
    }

    fun unbind() {
        glBindVertexArray(0)
    }

    override fun dispose() {
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
        glDeleteVertexArrays(vao)
    }
}