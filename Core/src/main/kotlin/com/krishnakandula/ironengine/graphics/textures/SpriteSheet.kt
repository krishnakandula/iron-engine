package com.krishnakandula.ironengine.graphics.textures

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.krishnakandula.ironengine.utils.JsonHelper

class SpriteSheet(
        rootFilePath: String,
        spriteSheetName: String,
        textureLoader: TextureLoader,
        jsonHelper: JsonHelper
) {

    private val texture: Texture = textureLoader.loadTexture(
            "${rootFilePath}${spriteSheetName}.png",
            1,
            false)
    private val sprites: Map<String, SpriteData>
    private val spriteSheetData: SpriteSheetData

    init {
        val dataFilePath = "${rootFilePath}${spriteSheetName}.json"
        spriteSheetData = jsonHelper.readFromFile(dataFilePath)
        sprites = spriteSheetData.frames.associateBy { frame -> frame.filename }
        texture.bind(1)
    }

    operator fun get(spriteName: String): Sprite? {
        val spriteData = sprites[spriteName] ?: return null
        val spriteSheetWidth = spriteSheetData.meta.size.w.toFloat()
        val spriteSheetHeight = spriteSheetData.meta.size.h.toFloat()

        return Sprite(
                this,
                spriteName,
                x1 = spriteData.frame.x.toFloat() / spriteSheetWidth,
                x2 = (spriteData.frame.x.toFloat() + spriteData.frame.w - 1) / spriteSheetWidth,
                y1 = spriteData.frame.y.toFloat() / spriteSheetHeight,
                y2 = (spriteData.frame.y.toFloat() + spriteData.frame.h - 1) / spriteSheetHeight,
                width = spriteData.frame.w.toFloat(),
                height = spriteData.frame.h.toFloat())
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
