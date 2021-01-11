package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Disposable

interface Scene : Disposable {

    fun update(deltaTime: Double)

    fun fixedUpdate(deltaTime: Double)

    fun onWindowSizeUpdated(width: Int, height: Int)

    fun onKeyEvent(key: Int, action: Int): Boolean
}