package com.krishnakandula.roguelike

import com.krishnakandula.ironengine.Window
import com.krishnakandula.ironengine.ecs.*
import com.krishnakandula.ironengine.graphics.Shader
import com.krishnakandula.ironengine.graphics.camera.Camera
import com.krishnakandula.ironengine.graphics.camera.OrthographicCamera
import com.krishnakandula.ironengine.graphics.rendering.SpriteBatchRenderer
import com.krishnakandula.ironengine.graphics.textures.SpriteSheet
import com.krishnakandula.ironengine.graphics.textures.TextureLoader
import com.krishnakandula.ironengine.physics.Transform
import com.krishnakandula.ironengine.utils.JsonHelper
import com.krishnakandula.ironengine.utils.toIsometric
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

    init {
        addSystem(SpriteBatchRenderer(shader, camera))
        addSystem(CameraMovementSystem(camera, window, cameraSpeed = 50f))

        val grassSprite = spriteSheet["Grass.png"]
        val rootTransform = Transform()
        val root = entityManager.createEntity()
        rootTransform.rotate(0f, 0f, 90f)
        componentManager.addComponent(root, rootTransform)

        // Generate grass tiles
        (0..2).forEach { row ->
            (0..2).forEach cols@{ col ->
                val position = Vector3f(
                    (row * 16).toFloat(),
                    (col * 16).toFloat(),
                    0f).toIsometric().isoCoords
                val grassTile = grassEntity(
                    "Grass.png", position)
                val grassTileTransform = componentManager.getComponent<Transform>(grassTile) ?: return@cols

                rootTransform.addChild(grassTileTransform)
            }
        }
    }

    fun grassEntity(spriteName: String, position: Vector3f): Entity {
        val grass = entityManager.createEntity()
        val sprite = spriteSheet[spriteName]
            ?: // TODO: Change this exception to a SpriteNotFoundException
            throw NullPointerException("Can't find sprite with name $spriteName")
        componentManager.addComponent(grass, sprite)
        componentManager.addComponent(grass, Transform(position))

        return grass
    }

    private class CameraMovementSystem(
        private val camera: Camera,
        private val window: Window,
        private val cameraSpeed: Float
    ) : System() {

        override fun update(deltaTime: Double) {
            super.update(deltaTime)
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
            }
        }
    }

    override fun update(deltaTime: Double) {
        camera.transform.rotate(0f, 0f, 1000f * deltaTime.toFloat())
        camera.updateView()
        shader.setMat4("view", camera.view)
        super.update(deltaTime)
    }
}
