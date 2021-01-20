package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Disposable

interface System : Disposable {

    fun onAddedToScene(scene: Scene) {

    }

    fun update(deltaTime: Double) {

    }

    fun fixedUpdate(deltaTime: Double) {

    }
}
