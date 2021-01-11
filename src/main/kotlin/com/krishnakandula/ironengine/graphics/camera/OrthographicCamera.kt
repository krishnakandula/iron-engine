package com.krishnakandula.ironengine.graphics.camera

import com.krishnakandula.ironengine.physics.Transform
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class OrthographicCamera(
    transform: Transform,
    view: Mat4,
    projection: Mat4,
    val widthRatio: Float,
    val heightRatio: Float,
    val near: Float,
    val far: Float
) : Camera(transform, view, projection) {

    companion object {

        private val DIRECTION = Vec3(0f, 0f, -1f)
        private val WORLD_UP = Vec3(0f, 1f, 0f)

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

            val projectionMatrix = glm.ortho(left, right, bottom, top, near, far)
            val widthRatio = width / screenWidth
            val heightRatio = height / screenHeight


            val transform = Transform(position = Vec3(0f), rotation = Vec3(0f), scale = Vec3(1f))

            return OrthographicCamera(
                transform = transform,
                view = Mat4(1f),
                projection = projectionMatrix,
                widthRatio = widthRatio,
                heightRatio = heightRatio,
                near = near,
                far = far
            )
        }
    }

    override fun update() {
        super.view = glm.lookAt(
            super.transform.position,
            super.transform.position + DIRECTION,
            WORLD_UP)
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

        super.projection = glm.ortho(-10.0f * ratio,10.0f * ratio, -10f, 10f, near, far)
    }
}