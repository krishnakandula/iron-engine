package com.krishnakandula.ironengine

import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.physics.Transform
import glm_.detail.Random
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import kotlin.math.cos
import kotlin.math.sin

class Game {

    class Boid(
        val transform: Transform,
        var model: Mat4 = Mat4(1f),
        var direction: Vec3 = Vec3(0f)
    ) {

        init {
            updateModel()
        }

        fun updateModel() {
            val roll = transform.rotation.z
            model = Mat4(1)
                .translate(transform.position)
                .rotate(glm.radians(roll), 0f, 0f, 1f)
                .scale(transform.scale)

            direction = Vec3(
                cos(glm.radians(roll + 90f)),
                sin(glm.radians(roll + 90f)),
                0f
            ).normalize()
        }
    }

    class BoidScene(private val window: Window) : Scene {

        private val cameraSpeed = 2f
        private var worldWidth = 20f
        private var worldHeight = 20f
        private val maxSpeed = 5f
        private var prevWidth = window.getDimensions().x
        private var prevHeight = window.getDimensions().y

        val triangleVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
        )
        val triangleIndices = intArrayOf(
            0, 1, 2,
        )

        val camera = OrthographicCamera.new(1600f, 1600f, 20f, 20f, 0f, 10f)
        val mesh = Mesh(triangleVertices, triangleIndices)
        val shader = Shader(
            "src/main/resources/shaders/vert.glsl",
            "src/main/resources/shaders/frag.glsl"
        )

        val boids: List<Boid>

        init {
            shader.use()

            shader.setMat4("view", camera.view)
            shader.setMat4("projection", camera.projection)
            shader.setMat4("model", Mat4(1))
            boids = (0..55).map { x ->
                val startPositionX = Random[-worldWidth / 2f, worldWidth / 2f]
                val startPositionY = Random[-worldHeight / 2f, worldHeight / 2f]
                // randomize rotation
                val roll = Random[0f, 360f]
                Boid(Transform(
                    Vec3(startPositionX, startPositionY, 0f),
                    Vec3(0f, 0f, -roll),
                    Vec3(.3f, .6f, 1)))
            }
        }

        override fun dispose() {
            mesh.dispose()
            shader.dispose()
        }

        override fun update(deltaTime: Double) {
            mesh.bind()
            boids.forEach { boid ->
                shader.setMat4("model", boid.model)
                glDrawElements(GL_TRIANGLES, mesh.indexCount, GL_UNSIGNED_INT, 0)
            }
            mesh.unbind()
        }

        override fun fixedUpdate(deltaTime: Double) {
            processInput(deltaTime)
            boids.forEach { boid ->
                boid.transform.translate(boid.direction * deltaTime * maxSpeed)
//                boid.transform.rotate(Vec3(0f, 0f, deltaTime) * 0.1f)
                val xBoundary = worldWidth / 2f
                val yBoundary = worldHeight / 2f

                // check if boid is past world boundary
                val direction = boid.direction
                val position = boid.transform.position

                if (position.x > xBoundary) {
                    val slope = direction.y / direction.x
                    val intercept = position.y - (slope * position.x)

                    boid.transform.position = when {
                        slope == 0f -> Vec3(-xBoundary, position.y, 0f)
                        slope < 0f -> Vec3(getInterceptCoordinateX(slope, intercept, yBoundary), yBoundary, 0f)
                        else -> Vec3(getInterceptCoordinateX(slope, intercept, -yBoundary), -yBoundary, 0f)
                    }
                } else if (position.x < -xBoundary) {
                    val slope = direction.y / direction.x
                    val intercept = position.y - (slope * position.x)

                    boid.transform.position = when {
                        slope == 0f -> Vec3(xBoundary, position.y, 0f)
                        slope < 0f -> Vec3(getInterceptCoordinateX(slope, intercept, -yBoundary), -yBoundary, 0f)
                        else -> Vec3(getInterceptCoordinateX(slope, intercept, yBoundary), yBoundary, 0f)
                    }
                }

                boid.updateModel()
            }
        }

        private fun getInterceptCoordinateX(slope: Float, intercept: Float, y: Float): Float = (y - intercept) / slope

        private fun processInput(deltaTime: Double) {
            var cameraTransformDirty = false
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vec3(0f, 1f, 0f) * cameraSpeed * deltaTime)
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vec3(-1f, 0f, 0f) * cameraSpeed * deltaTime)
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vec3(0f, -1f, 0f) * cameraSpeed * deltaTime)
                cameraTransformDirty = true
            }
            if (GLFW.glfwGetKey(window.windowId, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera.transform.translate(Vec3(1f, 0f, 0f) * cameraSpeed * deltaTime)
                cameraTransformDirty = true
            }

            if (cameraTransformDirty) {
                camera.update()
                shader.setMat4("view", camera.view)
            }
        }

        override fun onWindowSizeUpdated(width: Int, height: Int) {
            camera.onWindowSizeUpdated(width, height)
            camera.update()
            shader.setMat4("projection", camera.projection)

            // update world width
            val widthRatio = width.toFloat() / prevWidth
            val heightRatio = height.toFloat() / prevHeight

            worldWidth *= widthRatio
            worldHeight *= heightRatio
        }

        override fun onKeyEvent(key: Int, action: Int): Boolean {
            return false
        }
    }

    fun start() {
        val window = Window(1000, 1000, "Game")
        val sceneManager = SceneManager(1, window)
        sceneManager.push(BoidScene(window))

        while (!window.shouldClose()) {
            GL11.glClearColor(1f, 1f, 0f, 1f)
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

            sceneManager.update()

            window.swapBuffers()
            window.pollEvents()
        }
    }
}