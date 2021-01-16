package com.krishnakandula.ironengine.graphics.camera

import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import org.joml.Matrix4f
import org.joml.Vector3f

class OrthographicCamera(
    transform: Transform,
    view: Matrix4f,
    projection: Matrix4f,
    val widthRatio: Float,
    val heightRatio: Float,
    val near: Float,
    val far: Float
) : Camera(transform, view, projection) {

    companion object {

        private val DIRECTION = Vector3f(0f, 0f, -1f)
        private val WORLD_UP = Vector3f(0f, 1f, 0f)

        fun new(
            screenWidth: Float,
            screenHeight: Float,
            width: Float,
            height: Float,
            near: Float,
            far: Float
        ): OrthographicCamera {

            val right = width / 2f
            val left = -right
            val top = height / 2f
            val bottom = -top

            val projectionMatrix = Matrix4f().ortho(left, right, bottom, top, near, far)
            val widthRatio = width / screenWidth
            val heightRatio = height / screenHeight


            val transform = Transform(position = Vector3f(0f), rotation = Vector3f(0f), scale = Vector3f(1f))

            return OrthographicCamera(
                transform = transform,
                view = Matrix4f(),
                projection = projectionMatrix,
                widthRatio = widthRatio,
                heightRatio = heightRatio,
                near = near,
                far = far
            )
        }
    }

    override fun update() {
        super.view.lookAt(
            super.transform.position.clone(),
            super.transform.position.clone().add(DIRECTION),
            WORLD_UP.clone()
        )
    }

    override fun onWindowSizeUpdated(windowWidth: Int, windowHeight: Int) {
        val ratio = windowWidth.toFloat() / windowHeight.toFloat()
        println(ratio)
        val cameraWidth = windowWidth.toFloat() * widthRatio
        val cameraHeight = windowHeight.toFloat() * heightRatio


        val right = windowWidth / 2f
        val left = -right
        val top = windowHeight / 2f
        val bottom = -top

        super.projection.identity().ortho(-10.0f * ratio,10.0f * ratio, -10f, 10f, near, far)
    }
}