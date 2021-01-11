package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Disposable

interface Component : Disposable {

    override fun dispose() {
        // do nothing
    }
}