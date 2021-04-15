package com.krishnakandula.ironengine.graphics.textures

import org.lwjgl.opengl.GL13.GL_TEXTURE_2D
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL13.glBindTexture

class Texture internal constructor(
    val id: Int,
    val width: Int,
    val height: Int
) {

    fun bind(textureUnit: Int) {
        glActiveTexture(textureUnit)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Texture) {
            false
        } else {
            other.id == id
        }
    }

    override fun hashCode(): Int = id.hashCode()
}