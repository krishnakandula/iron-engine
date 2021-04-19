package com.krishnakandula.ironengine.graphics.textures

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.krishnakandula.ironengine.utils.JsonHelper
import org.lwjgl.opengl.GL11.GL_RGB

class SpriteSheet(
    rootFilePath: String,
    spriteSheetName: String,
    textureLoader: TextureLoader,
    jsonHelper: JsonHelper
) {

    val texture: Texture = textureLoader.loadTexture(
        "${rootFilePath}${spriteSheetName}.png",
        0,
        GL_RGB,
        false
    )
    private val sprites: Map<String, SpriteData>
    private val spriteSheetData: SpriteSheetData

    init {
        val dataFilePath = "${rootFilePath}${spriteSheetName}.json"
        spriteSheetData = jsonHelper.readFromFile(dataFilePath)
        sprites = spriteSheetData.frames.associateBy { frame -> frame.filename }
    }

    operator fun get(spriteName: String): Sprite? {
        val spriteData = sprites[spriteName] ?: return null
        val width = spriteSheetData.meta.size.w.toFloat()
        val height = spriteSheetData.meta.size.h.toFloat()

        return Sprite(
            this,
            spriteName,
            spriteData.frame.x / width,
            spriteData.frame.y / height,
            (spriteData.frame.x + spriteData.frame.w - 1) / width,
            (spriteData.frame.y + spriteData.frame.h - 1) / height
        )
    }

    fun getAllSprites(): Collection<SpriteData> = sprites.values

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Metadata(val image: String, val size: Size)
}
