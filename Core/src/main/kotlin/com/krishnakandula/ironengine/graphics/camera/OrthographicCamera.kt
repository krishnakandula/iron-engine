package com.krishnakandula.ironengine.graphics.camera

import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import org.joml.Matrix4f
import org.joml.Vector3f

class OrthographicCamera(
    transform: Transform,
    view: Matrix4f,
    projection: Matrix4f,
    val pixelToWorldRatio: Float,
    val near: Float,
    val far: Float
) : Camera(transform, view, projection) {

    companion object {

        private val DIRECTION = Vector3f(0f, 0f, -1f)
        private val WORLD_UP = Vector3f(0f, 1f, 0f)

        fun new(
            screenWidth: Float,
            screenHeight: Float,
            pixelToWorldRatio: Float, // how many pixels are in 1 world unit
            near: Float,
            far: Float
        ): OrthographicCamera {

            val cameraWidth = screenWidth / pixelToWorldRatio
            val cameraHeight = screenHeight / pixelToWorldRatio

            val right = cameraWidth / 2f
            val left = -right
            val top = cameraHeight / 2f
            val bottom = -top

            val projectionMatrix = Matrix4f().ortho(left, right, bottom, top, near, far)

            val transform = Transform(position = Vector3f(0f), rotation = Vector3f(0f), scale = Vector3f(1f))

            val camera = OrthographicCamera(
                transform = transform,
                view = Matrix4f(),
                projection = projectionMatrix,
                near = near,
                far = far,
                pixelToWorldRatio = pixelToWorldRatio
            )
            camera.updateView()

            return camera
        }
    }

    override fun updateView() {
        super.view.setLookAt(
            super.transform.position,
            super.transform.position.clone().add(DIRECTION),
            WORLD_UP
        )
    }

    override fun onWindowSizeUpdated(windowWidth: Int, windowHeight: Int) {
        val cameraWidth = windowWidth.toFloat() / pixelToWorldRatio
        val cameraHeight = windowHeight.toFloat() / pixelToWorldRatio

        super.projection.setOrtho(
            -cameraWidth / 2f,
            cameraWidth / 2f,
            -cameraHeight / 2f,
            cameraHeight / 2f,
            near,
            far
        )
    }
}