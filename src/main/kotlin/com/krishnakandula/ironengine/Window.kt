package com.krishnakandula.ironengine

import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class Window(width: Int, height: Int, title: String) {

    val windowId: Long

    init {
        GLFW.glfwInit()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)

        windowId = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowId == MemoryUtil.NULL) {
            GLFW.glfwTerminate()
            throw RuntimeException("Unable to create GLFW window")
        }
        GLFW.glfwMakeContextCurrent(windowId)

        // Initialize OpenGL
        if (GL.createCapabilities() == null) {
            GLFW.glfwTerminate()
            throw RuntimeException("Unable to create GL")
        }

        GL11.glViewport(0, 0, width, height)
    }

    fun getDimensions(): Vec2 {
        return MemoryStack.stackPush().use { stack ->
            val widthBuffer = stack.mallocInt(1)
            val heightBuffer = stack.mallocInt(1)
            GLFW.glfwGetFramebufferSize(windowId, widthBuffer, heightBuffer)

            Vec2(widthBuffer.get(), heightBuffer.get())
        }
    }

    fun getCursorPosition(): Vec2 {
        return MemoryStack.stackPush().use { stack ->
            val xBuffer = stack.mallocDouble(1)
            val yBuffer = stack.mallocDouble(1)
            GLFW.glfwGetCursorPos(windowId, xBuffer, yBuffer)

            Vec2(xBuffer.get(), yBuffer.get())
        }
    }

    fun getTime(): Double = GLFW.glfwGetTime()

    fun shouldClose(): Boolean = GLFW.glfwWindowShouldClose(windowId)

    fun setFrameBufferSizeCallback(callback: (window: Long, width: Int, height: Int) -> Unit) {
        GLFW.glfwSetFramebufferSizeCallback(windowId, callback)
    }

    fun setKeyCallback(callback: (window: Long, key: Int, scancode: Int, action: Int, mods: Int) -> Unit) {
        GLFW.glfwSetKeyCallback(windowId, callback)
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(windowId)
    }

    fun pollEvents() {
        GLFW.glfwPollEvents()
    }

    fun close() {
        GLFW.glfwSetWindowShouldClose(windowId, true)
    }
}