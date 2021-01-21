package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.Window
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.RenderingSystem
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.physics.Transform
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW


class Game {

    class BoidScene(private val window: Window) : Scene() {

        private val cameraSpeed = 10f
        private val worldWidth = 60f
        private val worldHeight = 60f

        val triangleVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
        )
        val triangleIndices = intArrayOf(
            0, 1, 2,
        )

        val lineVertices = floatArrayOf(
            0.0f, 0f, 0.0f,
            0.0f, 1f, 0.0f
        )
        val lineIndices = intArrayOf(0, 1)

        val camera = OrthographicCamera.new(1600f, 1600f, 100f, 0f, 10f)
        val boidMesh = Mesh(triangleVertices, triangleIndices, 3, 3)
        val lineMesh = Mesh(lineVertices, lineIndices, 3, 3)
        val shader = Shader(
            "Sample/src/main/resources/shaders/vert.glsl",
            "Sample/src/main/resources/shaders/frag.glsl"
        )

        init {
            (0..20000).forEach { _ ->
                val startPositionX = getRandInRange(-worldWidth / 2f, worldWidth / 2f)
                val startPositionY = getRandInRange(-worldHeight / 2f, worldHeight / 2f)

                // randomize rotation
                val roll = getRandInRange(0f, 360f)

                createBoid(
                    Vector3f(startPositionX, startPositionY, 0f),
                    Vector3f(0f, 0f, -roll),
                    Vector3f(.3f, .6f, 1f)
                )
            }

            addSystem(RenderingSystem(camera, shader))
            addSystem(MovementSystem(worldWidth / 2f, worldHeight / 2f))
        }

        private fun createBoid(position: Vector3f, rotation: Vector3f, scale: Vector3f) {
            val boid = entityManager.createEntity()
            componentManager.addComponent(boid, Transform(
                position = position,
                rotation = rotation,
                scale = scale
            ))
            componentManager.addComponent(boid, boidMesh)
            componentManager.addComponent(boid, MovementComponent())
        }

        override fun dispose() {
            boidMesh.dispose()
            shader.dispose()
            super.dispose()
        }

        private fun processInput(deltaTime: Double) {
            var cameraTransformDirty = false
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vector3f(0f, 1f, 0f).mul(cameraSpeed * deltaTime.toFloat()))
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vector3f(-1f, 0f, 0f).mul(cameraSpeed * deltaTime.toFloat()))
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vector3f(0f, -1f, 0f).mul(cameraSpeed * deltaTime.toFloat()))
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vector3f(1f, 0f, 0f).mul(cameraSpeed * deltaTime.toFloat()))
                cameraTransformDirty = true
            }

            if (cameraTransformDirty) {
                println(camera.transform.position)
                camera.updateView()
                shader.setMat4("view", camera.view)
            }
        }

        override fun onWindowSizeUpdated(width: Int, height: Int) {
            camera.onWindowSizeUpdated(width, height)
            camera.updateView()
        }

        override fun onKeyEvent(key: Int, action: Int): Boolean {
            return false
        }

        var cursorYPos: Float = window.getCursorPosition().y
    }

    fun start() {
        val window = Window(1000, 1000, "Game")
        val sceneManager = SceneManager(1, window)
        sceneManager.push(BoidScene(window))

        while (!window.shouldClose()) {
            sceneManager.update()
        }
    }
}

fun getRandInRange(min: Float, max: Float): Float {
    val rand = Math.random()
    return (((max - min) * rand) + min).toFloat()
}