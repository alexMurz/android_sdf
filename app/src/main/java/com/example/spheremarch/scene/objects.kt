package com.example.spheremarch.scene

import com.example.spheremarch.math.*
import kotlin.math.max
import kotlin.math.min

sealed interface Shape {
    fun distanceTo(offset: Vec3): Float
}

data class SphereShape(val radius: Float) : Shape {
    override fun distanceTo(offset: Vec3): Float = offset.len() - radius
}

data class BoxShape(val halfSize: Vec3) : Shape {
    override fun distanceTo(offset: Vec3): Float {
        val q = offset.abs - halfSize
        return q.max(vec3Zero).len() + min(maxOf(q.x, q.y, q.z), 0f)
    }
}

data class Material(
    val color: Vec4 = vec4(1f, 1f, 1f, 1f),
)

data class Object(
    val pos: Vec3 = vec3(0f, 0f, 0f),
    val rot: Vec3 = vec3(0f, 0f, 0f),
    val material: Int = -1,
    val shape: Shape,
) {
    fun distanceTo(o: Vec3): Float = shape.distanceTo(pos - o)
}