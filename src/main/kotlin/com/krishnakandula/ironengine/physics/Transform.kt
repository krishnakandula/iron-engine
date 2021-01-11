package com.krishnakandula.ironengine.physics

import glm_.vec3.Vec3

data class Transform(var position: Vec3,
                     var rotation: Vec3,
                     var scale: Vec3) {

    fun translate(translation: Vec3) {
        position = position + translation
    }

    fun rotate(rotation: Vec3) {
        this.rotation = this.rotation + rotation
    }

    fun scale(scale: Vec3) {
        this.scale = this.scale * scale
    }
}
