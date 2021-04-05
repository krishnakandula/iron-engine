package com.krishnakandula.ironengine.graphics.textures

import java.nio.ByteBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL30.glGenerateMipmap

import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

class TextureLoader {

    private val cache: MutableMap<String, Texture> = HashMap()

    fun loadTexture(
        filePath: String,
        activeTexture: Int,
        imageFormat: Int,
        flip: Boolean
    ): Texture {
        if (!cache.containsKey(filePath)) {
            stbi_set_flip_vertically_on_load(flip)
            val rawTexture: RawTexture = loadTextureData(filePath)
            val textureId: Int = glGenTextures()

            glActiveTexture(activeTexture)
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGB,
                rawTexture.width,
                rawTexture.height,
                0,
                imageFormat,
                GL_UNSIGNED_BYTE,
                rawTexture.data
            )
            glGenerateMipmap(GL_TEXTURE_2D)
            stbi_image_free(rawTexture.data)

            cache[filePath] = Texture(textureId, rawTexture.width, rawTexture.height)
        }

        return cache[filePath]!!
    }

    private fun loadTextureData(filePath: String): RawTexture {
        MemoryStack.stackPush().use { stack ->
            val width: IntBuffer = stack.mallocInt(1)
            val height: IntBuffer = stack.mallocInt(1)
            val numChannels: IntBuffer = stack.mallocInt(1)

            val data = stbi_load(filePath, width, height, numChannels, 0)
            if (data != null) {
                return RawTexture(width.get(), height.get(), numChannels.get(), data)
            }

            throw RuntimeException("Unable to load texture with path $filePath")
        }
    }

    private class RawTexture(
        val width: Int,
        val height: Int,
        val channels: Int,
        val data: ByteBuffer
    )
}