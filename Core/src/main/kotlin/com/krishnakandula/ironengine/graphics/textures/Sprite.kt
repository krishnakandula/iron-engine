package com.krishnakandula.ironengine.graphics.textures

import com.krishnakandula.ironengine.ecs.component.Component

class Sprite(val texture: Texture) : Component(TYPE_ID) {

    companion object {
        val TYPE_ID = getNewTypeId()
    }
}