package com.krishnakandula.ironengine.ecs

import com.krishnakandula.ironengine.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glViewport

class SceneManager(private val updateDepth: Byte,
                   private val window: Window
) {

    private val scenes: ArrayDeque<Scene> = ArrayDeque()
    private val fixedUpdateInterval = 1.0 / 100.0
    private var fixedUpdateTime = window.getTime()
    private var time = fixedUpdateTime

    init {
        window.setFrameBufferSizeCallback(this::frameBufferSizeCallback)
        window.setKeyCallback(this::keyCallback)
    }

    fun push(scene: Scene) {
        scenes.addFirst(scene)
    }

    fun pop() {
        scenes.removeFirst()
    }

    fun update() {
        GL11.glClearColor(0f, 1f, 1f, 1f)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

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

        window.swapBuffers()
        window.pollEvents()
    }

    private fun frameBufferSizeCallback(windowId: Long, width: Int, height: Int) {
        glViewport(0, 0, width,height)
        for (i in 0..scenes.lastIndex) {
            scenes[i].onWindowSizeUpdated(width, height)
        }
    }

    private fun keyCallback(windowId: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        var actionHandled = false
        for (i in 0..scenes.lastIndex) {
            if (scenes[i].onKeyEvent(key, action)) {
                actionHandled = true
                break
            }
        }

        if (!actionHandled && key == GLFW.GLFW_KEY_ESCAPE) {
            window.close()
        }
    }
}