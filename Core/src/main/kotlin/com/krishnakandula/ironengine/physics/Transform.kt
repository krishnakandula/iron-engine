package com.krishnakandula.ironengine.physics

import org.joml.Vector3f

data class Transform(var position: Vector3f,
                     var rotation: Vector3f,
                     var scale: Vector3f) {

    fun translate(translation: Vector3f) {
        position.add(translation)
    }

    fun rotate(rotation: Vector3f) {
        this.rotation.add(rotation)
    }

    fun scale(scale: Vector3f) {
        this.scale.mul(scale)
    }
}
