package com.krishnakandula.ironengine.ecs.component

import com.krishnakandula.ironengine.Disposable

abstract class Component(val typeId: Int) : Disposable {
    companion object {
        private var id: Int = 0

        fun getNewTypeId(): Int = id++
    }
}
