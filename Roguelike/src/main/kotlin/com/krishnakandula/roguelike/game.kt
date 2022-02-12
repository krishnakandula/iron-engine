package com.krishnakandula.roguelike

import com.krishnakandula.ironengine.Window
import com.krishnakandula.ironengine.ecs.Entity
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
import com.krishnakandula.ironengine.ecs.System
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.graphics.rendering.SpriteBatchRenderer
import com.krishnakandula.ironengine.graphics.textures.SpriteSheet
import com.krishnakandula.ironengine.graphics.textures.TextureLoader
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.JsonHelper
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

fun main() {
    val window = Window(1000, 1000, "Roguelike")
    val sceneManager = SceneManager(1, window)
    sceneManager.push(StartScene(window))
    while (!window.shouldClose()) {
        sceneManager.update()
    }
}

class StartScene(private val window: Window) : Scene() {
    val shader = Shader(
        "Roguelike/src/main/resources/shaders/vert.glsl",
        "Roguelike/src/main/resources/shaders/frag.glsl"
    )
    val camera = OrthographicCamera.new(2000f, 2000f, 10f, 0f, 10f)
    val spriteSheet = SpriteSheet(
        "Roguelike/src/main/resources/textures/",
        "spritesheet",
        TextureLoader(),
        JsonHelper()
    )
    private val cameraSpeed = 10f

    init {
        camera.transform.rotate(0f, 0f, 90f)
        camera.updateView()
        shader.setMat4("view", camera.view)
        addSystem(SpriteBatchRenderer(shader, camera))
        val heroSprite = spriteSheet["Grass.png"]
        var rootTransform: Transform? = null
        val entities = entityManager.createEntities(1)
        entities.forEachIndexed { i, enemy ->
            if (heroSprite != null) componentManager.addComponent(enemy, heroSprite)
            val transform = Transform(Vector3f(i.toFloat(), i.toFloat(), 0f))
            rootTransform?.addChild(transform)
            if (rootTransform == null) {
                rootTransform = transform
            }
            componentManager.addComponent(enemy, transform)
        }
//        addSystem(RotationSystem(entities[0]))
    }

    private class RotationSystem(private val entity: Entity) : System() {

        override fun fixedUpdate(deltaTime: Double) {
            super.fixedUpdate(deltaTime)
            val transform = componentManager?.getComponent<Transform>(entity) ?: return
            transform.rotate(0f, 0f, 5f * deltaTime.toFloat())
        }
    }

    override fun update(deltaTime: Double) {
        camera.transform.rotate(0f, 0f, 1000f * deltaTime.toFloat())
        camera.updateView()
        shader.setMat4("view", camera.view)
        super.update(deltaTime)
    }

    override fun fixedUpdate(deltaTime: Double) {
        super.fixedUpdate(deltaTime)
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
            camera.updateView()
            shader.setMat4("view", camera.view)
        }
    }
}
