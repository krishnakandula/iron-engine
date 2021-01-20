package com.krishnakandula.ironengine.physics.collisions

import com.krishnakandula.ironengine.physics.Line
import com.krishnakandula.ironengine.utils.clone
import org.joml.Vector3f
import kotlin.math.min
import kotlin.math.max

fun rayCastTriangle2D(line: Line, v0: Line, v1: Line, v2: Line): Vector3f? {
    val v0Intersect = lineSegmentIntersection2D(line, v0)
    if (v0Intersect != null) return v0Intersect
    val v1Intersect = lineSegmentIntersection2D(line, v1)
    if (v1Intersect != null) return v1Intersect
    return lineSegmentIntersection2D(line, v2)
}

fun lineSegmentIntersection2D(l0: Line, l1: Line): Vector3f? {
    val intersection: Vector3f = lineIntersection2D(l0, l1) ?: return null

    val l0MinX = min(l0.origin.x, l0.origin.x + l0.direction.x)
    val l0MinY = min(l0.origin.y, l0.origin.y + l0.direction.y)
    val l1MinX = min(l1.origin.x, l1.origin.x + l1.direction.x)
    val l1MinY = min(l1.origin.y, l1.origin.y + l1.direction.y)

    val l0MaxX = max(l0.origin.x, l0.origin.x + l0.direction.x)
    val l0MaxY = max(l0.origin.y, l0.origin.y + l0.direction.y)
    val l1MaxX = max(l1.origin.x, l1.origin.x + l1.direction.x)
    val l1MaxY = max(l1.origin.y, l1.origin.y + l1.direction.y)

    // check if the intersection is within the bounds of both of the line segments
    if (intersection.x < l0MinX || intersection.x > l0MaxX || intersection.x < l1MinX || intersection.x > l1MaxX) {
        return null
    }

    if (intersection.y < l0MinY || intersection.y > l0MaxY || intersection.y < l1MinY || intersection.y > l1MaxY) {
        return null
    }

    return intersection
}

fun lineIntersection2D(l0: Line, l1: Line): Vector3f? {
    val p0x = l0.origin.x
    val p1x = l1.origin.x
    val p0y = l0.origin.y
    val p1y = l1.origin.y
    val d0x = l0.direction.x
    val d1x = l1.direction.x
    val d0y = l0.direction.y
    val d1y = l1.direction.y

    val denom = (d0x * d1y) - (d1x * d0y)
    val numer = (d0x * (p0y - p1y)) + (p1x * d0y) - (p0x * d0y)
    val s = numer / denom

    return l1.origin.clone().add(l1.direction.clone().mul(s))
}
