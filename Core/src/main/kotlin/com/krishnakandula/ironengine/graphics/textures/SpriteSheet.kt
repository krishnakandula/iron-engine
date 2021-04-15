package com.krishnakandula.ironengine.graphics.textures

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import org.lwjgl.opengl.GL11.GL_RGB

class SpriteSheet(
    rootFilePath: String,
    spriteSheetName: String,
    textureLoader: TextureLoader
) {

    private val texture: Texture = textureLoader.loadTexture(
        "${rootFilePath}${spriteSheetName}.png",
        0,
        GL_RGB,
        false)
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val sprites: MutableMap<String, SpriteData> = HashMap()

    init {
        val dataFilePath = "${rootFilePath}${spriteSheetName}.json"
        val jsonData = File(dataFilePath).readText()
        val spriteSheetData = objectMapper.readValue(jsonData, SpriteSheetData::class.java)
        spriteSheetData.frames.forEach { frame ->
           sprites[frame.filename] = frame
        }
    }

    data class SpriteSheetData(
        val frames: Array<SpriteData>,
        val meta: Metadata
    )

    data class SpriteData(
        val filename: String,
        val frame: SpriteSize,
        val rotated: Boolean,
        val trimmed: Boolean,
        val spriteSourceSize: SpriteSize,
        val sourceSize: Size
    )

    data class SpriteSize(val x: Int, val y: Int, val w: Int, val h: Int)

    data class Size(val w: Int, val h: Int)

    data class Metadata(val image: String, val size: Size)
}
