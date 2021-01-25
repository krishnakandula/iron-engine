package com.krishnakandula.ironengine.graphics

import com.krishnakandula.ironengine.graphics.camera.Camera
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11C.GL_LINES
import org.lwjgl.opengl.GL11C.GL_POINTS
import org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11C.glDrawElements
import kotlin.math.atan2

class DebugRenderer(private val camera: Camera, private val shader: Shader) {

    object Line {
        val vertices: FloatArray = floatArrayOf(
            0.0f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
        )
        val indices: IntArray = intArrayOf(0, 1)
        val mesh: Mesh = Mesh(vertices, indices, 3, 3)
    }

    object Point {
        val vertices: FloatArray = floatArrayOf(
            0f, 0f, 0f
        )
        val indices: IntArray = intArrayOf(0)
        val mesh: Mesh = Mesh(vertices, indices, 3, 3)
    }

    fun drawLine(start: Vector3f, end: Vector3f) {
        val xDistance: Float = end.x - start.x
        val yDistance: Float = end.y - start.y
        val midPoint = Vector3f((xDistance / 2f) + start.x, (yDistance / 2f) + start.y, 0f)

        // calculate length
        val line: Vector3f = end.sub(start)
        val lineLength: Float = line.length()

        // calculate rotation in z axis
        val roll: Float = atan2(yDistance, xDistance) * (180f / 3.1415f)

        val modelMatrix = Matrix4f()
            .translate(midPoint)
            .rotate(Math.toRadians(roll - 90f), 0f, 0f, 1f)
            .scale(lineLength)

        camera.updateView()

        shader.use()
        shader.setMat4("model", modelMatrix)
        shader.setMat4("view", camera.view)
        shader.setMat4("projection", camera.projection)

        Line.mesh.bind()
        glDrawElements(GL_LINES, Line.mesh.indexCount, GL_UNSIGNED_INT, 0)
        Line.mesh.unbind()
    }

    fun drawPoint(position: Vector3f) {
        val modelMatrix: Matrix4f = Matrix4f()
            .translate(position)
            .scale(0.1f, 0.1f, 1f)

        camera.updateView()

        shader.use()
        shader.setMat4("model", modelMatrix)
        shader.setMat4("view", camera.view)
        shader.setMat4("projection", camera.projection)

        Line.mesh.bind()
        glDrawElements(GL_LINES, Line.mesh.indexCount, GL_UNSIGNED_INT, 0)
        Line.mesh.unbind()
    }
}