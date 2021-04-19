package com.krishnakandula.ironengine.graphics.textures

import com.krishnakandula.ironengine.ecs.component.Component

class Sprite(
    val spriteSheet: SpriteSheet,
    val name: String,
    val x1: Float,
    val x2: Float,
    val y1: Float,
    val y2: Float
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID = getNewTypeId()
    }
}