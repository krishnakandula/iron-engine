package com.krishnakandula.ironengine.graphics

import com.krishnakandula.ironengine.ecs.component.Component
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
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
    vararg vertexAttributes: VertexAttribute,
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }

    private val vao: Int = glGenVertexArrays()
    private val vbo: Int = glGenBuffers()
    private val ebo: Int = glGenBuffers()

    var vertexCount: Int = vertices.size
        private set

    var indexCount: Int = indices.size
        private set


    init {
        update(vertices = vertices, indices = indices, vertexAttributes = vertexAttributes)
    }

    fun update(
        vertices: FloatArray,
        indices: IntArray,
        usage: Int = GL_STATIC_DRAW,
        vararg vertexAttributes: VertexAttribute
    ) {
        setVertices(vertices, usage, vertexAttributes = vertexAttributes)
        setIndices(indices, usage)
    }

    private fun setVertices(
        vertices: FloatArray,
        usage: Int = GL_STATIC_DRAW,
        vararg vertexAttributes: VertexAttribute
    ) {
        this.vertexCount = vertices.size
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, usage)
        vertexAttributes.forEach { vertexAttrib ->
            glVertexAttribPointer(
                vertexAttrib.index,
                vertexAttrib.size,
                vertexAttrib.type,
                vertexAttrib.normalized,
                vertexAttrib.stride,
                vertexAttrib.pointer
            )
            glEnableVertexAttribArray(vertexAttrib.index)
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun setIndices(indices: IntArray, usage: Int = GL_STATIC_DRAW) {
        this.indexCount = indices.size
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage)
    }

    fun bind() {
        glBindVertexArray(vao)
    }

    fun unbind() {
        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun dispose() {
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
        glDeleteVertexArrays(vao)
    }
}