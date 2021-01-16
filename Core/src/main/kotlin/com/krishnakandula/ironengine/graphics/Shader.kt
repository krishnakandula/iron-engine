package com.krishnakandula.ironengine.graphics

import com.krishnakandula.ironengine.Disposable
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20C
import org.lwjgl.system.MemoryStack

class Shader(vertexShaderPath: String, fragmentShaderPath: String) : Disposable {

    val id: Int

    init {
        val vertexShaderSource = readShaderSourceFromFile(vertexShaderPath)
        val fragmentShaderSource = readShaderSourceFromFile(fragmentShaderPath)
        val vertexShader = createAndCompileShader(vertexShaderSource, GL20.GL_VERTEX_SHADER)
        val fragmentShader = createAndCompileShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER)

        id = createShaderProgram(vertexShader, fragmentShader)
    }

    fun use() {
        GL20.glUseProgram(id)
    }

    fun detach() {
        GL20.glUseProgram(0)
    }

    fun setBoolean(name: String, value: Boolean) {
        val location = GL20C.glGetUniformLocation(id, name)
        GL20C.glUniform1i(location, if (value) 1 else 0)
    }

    fun setInt(name: String, value: Int) {
        val location = GL20C.glGetUniformLocation(id, name)
        GL20C.glUniform1i(location, value)
    }

    fun setFloat(name: String, value: Float) {
        val location = GL20C.glGetUniformLocation(id, name)
        GL20C.glUniform1f(location, value)
    }

    fun setMat4(name: String, value: Matrix4f) {
        val location = GL20C.glGetUniformLocation(id, name)
        MemoryStack.stackPush().use { stack ->
            val buffer = value.get(stack.mallocFloat(16))
            GL20C.glUniformMatrix4fv(location, false, buffer)
        }
    }

    override fun dispose() {
        GL20.glDeleteProgram(id)
    }

    private fun readShaderSourceFromFile(shaderPath: String): String {
        val shaderSource = StringBuilder()
        try {
            BufferedReader(FileReader(shaderPath)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    shaderSource.append(line).append('\n')
                }
                return shaderSource.toString()
            }
        } catch (e: IOException) {
            System.err.println("Unable to read shader from path$shaderPath")
            throw RuntimeException(e)
        }
    }

    private fun createAndCompileShader(shaderSource: String, shaderType: Int): Int {
        val shader = GL20.glCreateShader(shaderType)
        GL20.glShaderSource(shader, shaderSource)
        GL20.glCompileShader(shader)
        MemoryStack.stackPush().use { memoryStack ->
            val success = memoryStack.mallocInt(1)
            GL20.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, success)
            if (success[0] == GL11.GL_FALSE) {
                val infoLog = GL20.glGetShaderInfoLog(shader)
                throw RuntimeException("Unable to compile shader $shaderSource $infoLog")
            }
        }
        return shader
    }

    private fun createShaderProgram(vertexShader: Int, fragmentShader: Int): Int {
        val shaderProgram = GL20.glCreateProgram()
        GL20.glAttachShader(shaderProgram, vertexShader)
        GL20.glAttachShader(shaderProgram, fragmentShader)
        GL20.glLinkProgram(shaderProgram)

        MemoryStack.stackPush().use { memoryStack ->
            val success = memoryStack.mallocInt(1)
            GL20.glGetProgramiv(shaderProgram, GL20.GL_LINK_STATUS, success)
            if (success[0] == GL11.GL_FALSE) {
                val infoLog = GL20.glGetProgramInfoLog(shaderProgram)
                throw RuntimeException("Unable to create shader program $infoLog")
            }
        }

        // We can delete the shaders because they're attached to the program
        GL20.glDeleteShader(vertexShader)
        GL20.glDeleteShader(fragmentShader)

        return shaderProgram
    }
}