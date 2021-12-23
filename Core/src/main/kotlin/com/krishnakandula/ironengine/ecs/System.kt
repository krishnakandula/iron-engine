package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Disposable
import com.krishnakandula.ironengine.ecs.component.ComponentManager

abstract class System : Disposable {

    protected var componentManager: ComponentManager? = null
    protected var entityManager: EntityManager? = null

    open fun onAddedToScene(scene: Scene) {
       componentManager = scene.componentManager
       entityManager = scene.entityManager
    }

    open fun onRemovedFromScene(scene: Scene) {
        componentManager = null
        entityManager = null
    }

    open fun update(deltaTime: Double) {

    }

    open fun fixedUpdate(deltaTime: Double) {

    }
}
