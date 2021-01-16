package com.krishnakandula.ironengine

import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.clone
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL11.glLineWidth
import kotlin.math.cos
import kotlin.math.sin

class Game {

    class Boid(
        val transform: Transform,
        var model: Matrix4f = Matrix4f(),
        var direction: Vector3f = Vector3f(0f)
    ) {

        init {
            updateModel()
        }

        fun updateModel() {
            val roll = transform.rotation.z
            direction.x = cos(Math.toRadians(roll + 90f))
            direction.y = sin(Math.toRadians(roll + 90f))
            direction.z = 0f
            direction.normalize()

            model.identity()
                .translate(transform.position)
                .rotate(Math.toRadians(roll), 0f, 0f, 1f)
                .scale(transform.scale)
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

        val lineVertices = floatArrayOf(
            0.0f, -0.5f, 0.0f,
            0.0f, 2.0f, 0.0f
        )
        val lineIndices = intArrayOf(0, 1)

        val camera = OrthographicCamera.new(1600f, 1600f, 20f, 20f, 0f, 10f)
        val boidMesh = Mesh(triangleVertices, triangleIndices, 3, 3)
        val lineMesh = Mesh(lineVertices, lineIndices, 3, 3)
        val shader = Shader(
            "src/main/resources/shaders/vert.glsl",
            "src/main/resources/shaders/frag.glsl"
        )

        val boids: List<Boid>

        init {
            shader.use()

            shader.setMat4("view", camera.view)
            shader.setMat4("projection", camera.projection)
            boids = List(500) { x ->
                val startPositionX = getRandInRange(-worldWidth / 2f, worldWidth / 2f)
                val startPositionY = getRandInRange(-worldHeight / 2f, worldHeight / 2f)

                // randomize rotation
                val roll = getRandInRange(0f, 360f)
                Boid(
                    Transform(
                        Vector3f(startPositionX, startPositionY, 0f),
                        Vector3f(0f, 0f, -roll),
                        Vector3f(.3f, .6f, 1f)
                    )
                )
            }
        }

        override fun dispose() {
            boidMesh.dispose()
            shader.dispose()
        }

        override fun update(deltaTime: Double) {
            println(1.0 / deltaTime)
            boidMesh.bind()
            boids.forEach { boid ->
                shader.setMat4("model", boid.model)
                glDrawElements(GL_TRIANGLES, boidMesh.indexCount, GL_UNSIGNED_INT, 0)
            }
            boidMesh.unbind()

            lineMesh.bind()
            glLineWidth(2f)
            boids.forEach { boid ->
                shader.setMat4("model", boid.model)
                glDrawElements(GL_LINES, lineMesh.indexCount, GL_UNSIGNED_INT, 0)
            }
            lineMesh.unbind()
        }

        override fun fixedUpdate(deltaTime: Double) {
            processInput(deltaTime)
            boids.parallelStream().forEach { boid ->
                boid.transform.translate(boid.direction.clone().mul(deltaTime.toFloat() * maxSpeed))
                val xBoundary = worldWidth / 2f
                val yBoundary = worldHeight / 2f

                // check if boid is past world boundary
                val position = boid.transform.position
                if (position.x > xBoundary || position.x < -xBoundary) {
                    position.x = -position.x
                } else if (position.y > yBoundary || position.y < -yBoundary) {
                    position.y = -position.y
                }

                boid.updateModel()
            }
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

            prevWidth = width.toFloat()
            prevHeight = height.toFloat()
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

fun getRandInRange(min: Float, max: Float): Float {
    val rand = Math.random()
    return (((max - min) * rand) + min).toFloat()
}