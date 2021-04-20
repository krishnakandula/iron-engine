package com.krishnakandula.ironengine.graphics

data class VertexAttribute(
    val index: Int,
    val size: Int,
    val type: Int,
    val normalized: Boolean,
    val stride: Int,
    val pointer: Long
)
