package com.krishnakandula.ironengine.sample

import com.krishnakandula.ironengine.Window
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
import com.krishnakandula.ironengine.graphics.Mesh
import com.krishnakandula.ironengine.graphics.RenderingSystem
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.physics.Line
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.physics.collisions.rayCastTriangle2D
import com.krishnakandula.ironengine.utils.clone
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK
import org.lwjgl.opengl.GL11.GL_LINE
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL11.glLineWidth
import org.lwjgl.opengl.GL11.glPolygonMode

class Game {

    class BoidScene(private val window: Window) : Scene() {

        private val cameraSpeed = 10f
        private val worldWidth = 40f
        private val worldHeight = 40f
        private val maxSpeed = 2f

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
            (0..2).forEach { _ ->
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
        }

        private fun createBoid(position: Vector3f, rotation: Vector3f, scale: Vector3f) {
            val boid = entityManager.createEntity()
            componentManager.addComponent(boid, Transform(
                position = position,
                rotation = rotation,
                scale = scale
            ))
            componentManager.addComponent(boid, boidMesh)
        }

        override fun dispose() {
            boidMesh.dispose()
            shader.dispose()
            super.dispose()
        }

//        override fun update(deltaTime: Double) {
////            println(1.0 / deltaTime)
//            boidMesh.bind()
//
//            boidMesh.unbind()
//
//            val boid1 = boids[0]
//            val boid2 = boids[1]
//
//            val v0 = Vector3f(-0.5f, -0.5f, 0.0f)
//            val v1 = Vector3f(0.0f, 0.5f, 0.0f)
//            val v2 = Vector3f(0.5f, -0.5f, 0.0f)
//
//            var intersected = true
//            var intersect = rayCastTriangle2D(
//                Line(boid1.transform.position.clone(), boid1.direction.clone().mul(5f)),
//                Line(v0.clone().add(boid2.transform.position), v1.clone().sub(v0)),
//                Line(v1.clone().add(boid2.transform.position), v2.clone().sub(v1)),
//                Line(v2.clone().add(boid2.transform.position), v0.clone().sub(v2))
//            )
//
//            if (intersect == null) {
//                intersected = false
//                intersect = boid1.transform.position.clone().add(boid1.direction.clone().normalize().mul(5f))
//            }
//
//            val intersectLine = intersect!!.clone().sub(boid1.transform.position)
//            val model = Matrix4f()
//                .translate(boid1.transform.position.clone())
//                .rotate(Math.toRadians(boid1.transform.rotation.z), 0f, 0f, 1f)
//                .scale(1f, intersectLine.length(), 1f)
//            shader.setMat4("model", model)
//            lineMesh.bind()
//            glLineWidth(if (intersected) 5f else 1f)
//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
//            glDrawElements(GL_LINES, lineMesh.indexCount, GL_UNSIGNED_INT, 0)
//            lineMesh.unbind()
//
//            // Move boid
////            val rotateZ: Float = getRandInRange(-15f, 15f)
////            boid1.transform.rotate(Vector3f(0f, 0f, rotateZ * deltaTime.toFloat() * 20f))
////            boid1.updateModel()
//
//        }

//        override fun fixedUpdate(deltaTime: Double) {
//            processInput(deltaTime)
//            boids.forEachIndexed { index, boid ->
//                if (index == 0) {
//                    boid.transform.translate(boid.direction.clone().mul(deltaTime.toFloat() * maxSpeed))
//                    val xBoundary = worldWidth / 2f
//                    val yBoundary = worldHeight / 2f
//
//                    // check if boid is past world boundary
//                    val position = boid.transform.position
//                    if (position.x > xBoundary || position.x < -xBoundary) {
//                        position.x = -position.x
//                    } else if (position.y > yBoundary || position.y < -yBoundary) {
//                        position.y = -position.y
//                    }
//
//                    boid.updateModel()
//                }
//
//            }
//        }

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
            shader.setMat4("projection", camera.projection)
        }

        override fun onKeyEvent(key: Int, action: Int): Boolean {
            return false
        }

        var cursorYPos: Float = window.getCursorPosition().y

//        override fun onCursorPositionChanged(xPosition: Double, yPosition: Double): Boolean {
//            // y pos determines roll
//            val yDiff = yPosition - cursorYPos
//            boids[0].transform.rotate(Vector3f(0f, 0f, -yDiff.toFloat()))
//            cursorYPos = yPosition.toFloat()
//            return true
//        }
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