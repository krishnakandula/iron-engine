package com.krishnakandula.ironengine.graphics.textures

import com.krishnakandula.ironengine.ecs.component.Component

class Texture internal constructor(
    val id: Int,
    val width: Int,
    val height: Int
) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID: Int = getNewTypeId()
    }
}