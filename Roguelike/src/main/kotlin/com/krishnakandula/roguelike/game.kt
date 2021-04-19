package com.krishnakandula.roguelike

import com.krishnakandula.ironengine.Window
import com.krishnakandula.ironengine.ecs.Scene
import com.krishnakandula.ironengine.ecs.SceneManager
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
    val window = Window(500, 500, "Roguelike")
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
    val camera = OrthographicCamera.new(2000f, 2000f, 100f, 0f, 10f)
    val spriteSheet = SpriteSheet(
        "Roguelike/src/main/resources/textures/",
        "spritesheet",
        TextureLoader(),
        JsonHelper())
    private val cameraSpeed = 10f
    init {
        val heroSprite = spriteSheet["Player.png"]

        (0..10).forEach {
            val enemy = entityManager.createEntity()
            if (heroSprite != null) componentManager.addComponent(enemy, heroSprite)
            componentManager.addComponent(enemy, Transform(Vector3f(it.toFloat(), it.toFloat(), 0f)))
            addSystem(SpriteBatchRenderer(shader, camera))
        }
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
            println(camera.transform.position)
            camera.updateView()
            shader.setMat4("view", camera.view)
        }
    }
}
