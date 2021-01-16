package com.krishnakandula.ironengine.graphics.camera

import com.krishnakandula.ironengine.physics.Transform
import org.joml.Matrix4f

abstract class Camera(
    val transform: Transform,
    var view: Matrix4f,
    var projection: Matrix4f
) {

    abstract fun update()

    abstract fun onWindowSizeUpdated(windowWidth: Int, windowHeight: Int)

}
