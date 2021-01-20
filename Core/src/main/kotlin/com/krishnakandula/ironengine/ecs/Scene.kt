package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Disposable
import com.krishnakandula.ironengine.ecs.component.ComponentManager

abstract class Scene: Disposable {

    val componentManager: ComponentManager = ComponentManager()

    val entityManager: EntityManager = EntityManager()

    private val systems: MutableList<System> = mutableListOf()

    open fun update(deltaTime: Double) {
        for (i in 0..systems.lastIndex) {
            systems[i].update(deltaTime)
        }
    }

    open fun fixedUpdate(deltaTime: Double) {
        for (i in 0..systems.lastIndex) {
            systems[i].fixedUpdate(deltaTime)
        }
    }

    open fun onWindowSizeUpdated(width: Int, height: Int) {
    }

    open fun onKeyEvent(key: Int, action: Int): Boolean = false

    open fun onCursorPositionChanged(xPosition: Double, yPosition: Double): Boolean = false

    protected fun addSystem(system: System) {
        systems.add(system)
        system.onAddedToScene(this)
    }
}