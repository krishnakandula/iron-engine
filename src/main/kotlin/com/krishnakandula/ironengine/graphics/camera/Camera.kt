package com.krishnakandula.ironengine.graphics.camera

import com.krishnakandula.ironengine.physics.Transform
import glm_.mat4x4.Mat4

abstract class Camera(
    val transform: Transform,
    var view: Mat4,
    var projection: Mat4
) {

    abstract fun update()

    abstract fun onWindowSizeUpdated(windowWidth: Int, windowHeight: Int)

}
