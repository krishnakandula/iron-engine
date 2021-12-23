package com.krishnakandula.ironengine.graphics.textures

import org.lwjgl.opengl.GL11.*
import java.nio.ByteBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL30.GL_RG
import org.lwjgl.opengl.GL30.glGenerateMipmap

import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

class TextureLoader {

    private val cache: MutableMap<String, Texture> = HashMap()
    private val channelsToImageFormatMap = mapOf(
        Pair(1, GL_RED),
        Pair(2, GL_RG),
        Pair(3, GL_RGB),
        Pair(4, GL_RGBA)
    )

    fun loadTexture(
        filePath: String,
        activeTexture: Int,
        flip: Boolean
    ): Texture {
        if (!cache.containsKey(filePath)) {
            stbi_set_flip_vertically_on_load(flip)
            val rawTexture: RawTexture = loadTextureData(filePath)
            val textureId: Int = glGenTextures()
            val imageFormat = channelsToImageFormatMap[rawTexture.channels] ?:
            throw RuntimeException("Unable to load texture with path $filePath. Invalid" +
                    "number of channels read.")

            glActiveTexture(activeTexture)
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                imageFormat,
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
                ?: throw RuntimeException("Unable to load texture with path $filePath")

            return RawTexture(width.get(), height.get(), numChannels.get(), data)
        }
    }

    private class RawTexture(
        val width: Int,
        val height: Int,
        val channels: Int,
        val data: ByteBuffer
    )
}