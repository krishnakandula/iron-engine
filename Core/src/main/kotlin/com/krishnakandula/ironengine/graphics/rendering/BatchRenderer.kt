package com.krishnakandula.ironengine.graphics.rendering

import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.ecs.component.Archetype
import com.krishnakandula.ironengine.ecs.component.ComponentManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.textures.Texture
import com.krishnakandula.ironengine.physics.Transform

class BatchRenderer : System {

    private lateinit var componentManager: ComponentManager
    private val requiredComponents: Archetype = Archetype(listOf(
        Transform.TYPE_ID,
        Mesh.TYPE_ID,
        Texture.TYPE_ID
    ))

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.componentManager = scene.componentManager
    }
}