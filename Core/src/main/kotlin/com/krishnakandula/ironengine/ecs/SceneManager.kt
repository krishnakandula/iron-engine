package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.glViewport

class SceneManager(private val updateDepth: Byte,
                   private val window: Window
) {

    private val scenes: ArrayDeque<Scene> = ArrayDeque()
    private val fixedUpdateInterval = 1.0 / 100.0
    private var fixedUpdateTime = window.getTime()
    private var time = fixedUpdateTime

    init {
        window.setFrameBufferSizeCallback { _, width, height ->
            glViewport(0, 0, width,height)
            scenes.forEach { scene -> scene.onWindowSizeUpdated(width, height) }
        }
        window.setKeyCallback { _, key, _, action, _ ->
            var actionHandled = false
            scenes.forEach { scene ->
                if (scene.onKeyEvent(key, action)) {
                    actionHandled = true
                    return@forEach
                }
            }

            if (!actionHandled && key == GLFW.GLFW_KEY_ESCAPE) {
                window.close()
            }
        }
    }

    fun push(scene: Scene) {
        scenes.addFirst(scene)
    }

    fun pop() {
        scenes.removeFirst()
    }

    fun update() {
        val currentTime = window.getTime()
        val deltaTime = currentTime - time

        for (sceneIndex in 0 until updateDepth) {
            scenes[sceneIndex].update(deltaTime)
        }

        time = currentTime
        while (fixedUpdateTime < time) {
            fixedUpdateTime += fixedUpdateInterval
            for (sceneIndex in 0 until updateDepth) {
                scenes[sceneIndex].fixedUpdate(fixedUpdateInterval)
            }
        }
    }
}