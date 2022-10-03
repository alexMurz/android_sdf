@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.example.spheremarch.math

import android.graphics.Color
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sqrt


data class Vec2(
    var x: Float,
    var y: Float,
) {
    var r: Float by ::x
    var g: Float by ::y

    fun set(x: Float = this.x, y: Float = this.y) {
        this.x = x
        this.y = y
    }

    override fun toString(): String = "[$x, $y]"
}

data class Vec3(
    var x: Float,
    var y: Float,
    var z: Float,
) {
    var r: Float by ::x
    var g: Float by ::y
    var b: Float by ::z

    fun set(x: Float = this.x, y: Float = this.y, z: Float = this.z) {
        this.x = x
        this.y = y
        this.z = z
    }

    override fun toString(): String = "[$x, $y, $z]"
}

data class Vec4(
    var x: Float,
    var y: Float,
    var z: Float,
    var w: Float,
) {
    var r: Float by ::x
    var g: Float by ::y
    var b: Float by ::z
    var a: Float by ::w

    fun set(x: Float = this.x, y: Float = this.y, z: Float = this.z, w: Float = this.w) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    override fun toString(): String = "[$x, $y, $z, $w]"
}

/////////
// Constructors
inline fun vec4(x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 1f) = Vec4(x, y, z, w)
inline fun vec4(v: Vec2, z: Float = 0f, w: Float = 1f) = vec4(v.x, v.y, z, w)
inline fun vec4(v: Vec3, w: Float = 1f) = vec4(v.x, v.y, v.z, w)
inline fun vec4(v: Vec4) = v

inline fun vec3(x: Float = 0f, y: Float = 0f, z: Float = 0f) = Vec3(x, y, z)
inline fun vec3(v: Vec2, z: Float = 0f) = vec3(v.x, v.y, z)
inline fun vec3(v: Vec3) = v

inline fun vec2(x: Float = 0f, y: Float = 0f) = Vec2(x, y)
inline fun vec2(v: Vec2) = v

/////////
// consts
val vec4Zero; get() = vec4(0f, 0f, 0f, 0f)
val vec3Zero; get() = vec3(0f, 0f, 0f)
val vec2Zero; get() = vec2(0f, 0f)

val vec4One; get() = vec4(1f, 1f, 1f, 1f)
val vec3One; get() = vec3(1f, 1f, 1f)
val vec2One; get() = vec2(1f, 1f)

/////////
// Negate
inline operator fun Vec4.unaryMinus() = vec4(-x, -y, -z, -w)
inline operator fun Vec3.unaryMinus() = vec3(-x, -y, -z)
inline operator fun Vec2.unaryMinus() = vec2(-x, -y)

/////////
// Add math
inline operator fun Vec4.plus(v: Vec4) = vec4(x + v.x, y + v.y, z + v.z, w + v.w)
inline operator fun Vec4.plus(v: Vec3) = vec4(x + v.x, y + v.y, z + v.z, w)
inline operator fun Vec4.plus(v: Vec2) = vec4(x + v.x, y + v.y, z, w)

inline operator fun Vec3.plus(v: Vec3) = vec3(x + v.x, y + v.y, z + v.z)
inline operator fun Vec3.plus(v: Vec2) = vec3(x + v.x, y + v.y, z)

inline operator fun Vec2.plus(v: Vec2) = vec2(x + v.x, y + v.y)

inline operator fun Vec4.plusAssign(v: Vec4) = set(x + v.x, y + v.y, z + v.z, w + v.w)
inline operator fun Vec4.plusAssign(v: Vec3) = set(x + v.x, y + v.y, z + v.z, w)
inline operator fun Vec4.plusAssign(v: Vec2) = set(x + v.x, y + v.y, z, w)

inline operator fun Vec3.plusAssign(v: Vec3) = set(x + v.x, y + v.y, z + v.z)
inline operator fun Vec3.plusAssign(v: Vec2) = set(x + v.x, y + v.y, z)

inline operator fun Vec2.plusAssign(v: Vec2) = set(x + v.x, y + v.y)

/////////
// Sub math
inline operator fun Vec4.minus(v: Vec4) = this + (-v)
inline operator fun Vec4.minus(v: Vec3) = this + (-v)
inline operator fun Vec4.minus(v: Vec2) = this + (-v)

inline operator fun Vec3.minus(v: Vec3) = this + (-v)
inline operator fun Vec3.minus(v: Vec2) = this + (-v)

inline operator fun Vec2.minus(v: Vec2) = this + (-v)

/////////
// Mul math
inline operator fun Float.times(v: Vec4) = vec4(this * v.x, this * v.y, this * v.z, this * v.w)
inline operator fun Vec4.times(v: Float) = vec4(x * v, y * v, z * v, w * v)
inline operator fun Vec4.times(v: Vec4) = vec4(x * v.x, y * v.y, z * v.z, w * v.w)
inline operator fun Vec4.times(v: Vec3) = vec4(x * v.x, y * v.y, z * v.z, w)
inline operator fun Vec4.times(v: Vec2) = vec4(x * v.x, y * v.y, z, w)

inline operator fun Float.times(v: Vec3) = vec3(this * v.x, this * v.y, this * v.z)
inline operator fun Vec3.times(v: Float) = vec3(x * v, y * v, z * v)
inline operator fun Vec3.times(v: Vec3) = vec3(x * v.x, y * v.y, z * v.z)
inline operator fun Vec3.times(v: Vec2) = vec3(x * v.x, y * v.y, z)

inline operator fun Float.times(v: Vec2) = vec2(this * v.x, this * v.y)
inline operator fun Vec2.times(v: Float) = vec2(x * v, y * v)
inline operator fun Vec2.times(v: Vec2) = vec2(x * v.x, y * v.y)

inline operator fun Vec4.timesAssign(v: Float) = set(x * v, y * v, z * v, w * v)
inline operator fun Vec4.timesAssign(v: Vec4) = set(x * v.x, y * v.y, z * v.z, w * v.w)
inline operator fun Vec4.timesAssign(v: Vec3) = set(x * v.x, y * v.y, z * v.z, w)
inline operator fun Vec4.timesAssign(v: Vec2) = set(x * v.x, y * v.y, z, w)

inline operator fun Vec3.timesAssign(v: Float) = set(x * v, y * v, z * v)
inline operator fun Vec3.timesAssign(v: Vec3) = set(x * v.x, y * v.y, z * v.z)
inline operator fun Vec3.timesAssign(v: Vec2) = set(x * v.x, y * v.y, z)

inline operator fun Vec2.timesAssign(v: Float) = set(x * v, y * v)
inline operator fun Vec2.timesAssign(v: Vec2) = set(x * v.x, y * v.y)

/////////
// Div math
inline operator fun Float.div(v: Vec4) = vec4(this / v.x, this / v.y, this / v.z, this / v.w)
inline operator fun Vec4.div(v: Float) = vec4(x / v, y / v, z / v, w / v)
inline operator fun Vec4.div(v: Vec4) = vec4(x / v.x, y / v.y, z / v.z, w / v.w)
inline operator fun Vec4.div(v: Vec3) = vec4(x / v.x, y / v.y, z / v.z, w)
inline operator fun Vec4.div(v: Vec2) = vec4(x / v.x, y / v.y, z, w)

inline operator fun Float.div(v: Vec3) = vec3(this / v.x, this / v.y, this / v.z)
inline operator fun Vec3.div(v: Float) = vec3(x / v, y / v, z / v)
inline operator fun Vec3.div(v: Vec3) = vec3(x / v.x, y / v.y, z / v.z)
inline operator fun Vec3.div(v: Vec2) = vec3(x / v.x, y / v.y, z)

inline operator fun Float.div(v: Vec2) = vec2(this / v.x, this / v.y)
inline operator fun Vec2.div(v: Float) = vec2(x / v, y / v)
inline operator fun Vec2.div(v: Vec2) = vec2(x / v.x, y / v.y)

/////////
// Dot product
inline fun Vec4.dot(v: Vec4) = x * v.x + y * v.y + z * v.z + w * v.w
inline fun Vec3.dot(v: Vec3) = x * v.x + y * v.y + z * v.z
inline fun Vec2.dot(v: Vec2) = x * v.x + y * v.y

/////////
// Dot product
inline fun Vec3.cross(v: Vec3) = vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

/////////
// Length
inline fun Vec4.len2() = dot(this)
inline fun Vec4.len() = sqrt(len2())

inline fun Vec3.len2() = dot(this)
inline fun Vec3.len() = sqrt(len2())

inline fun Vec2.len2() = dot(this)
inline fun Vec2.len() = sqrt(len2())

/////////
// Normalize
val Vec4.normalized: Vec4
    inline get() = this / len()
val Vec3.normalized: Vec3
    inline get() = this / len()
val Vec2.normalized: Vec2
    inline get() = this / len()

inline fun Vec4.normalizeInPlace(): Vec4 {
    val l = 1f / len()
    set(x * l, y * l, z * l, w * l)
    return this
}

inline fun Vec3.normalizeInPlace(): Vec3 {
    val l = 1f / len()
    set(x * l, y * l, z * l)
    return this
}

inline fun Vec2.normalizeInPlace(): Vec2 {
    val l = 1f / len()
    set(x * l, y * l)
    return this
}

/////////
// Absolute
val Vec4.abs: Vec4
    inline get() = vec4(x.absoluteValue, y.absoluteValue, z.absoluteValue, w.absoluteValue)
val Vec3.abs: Vec3
    inline get() = vec3(x.absoluteValue, y.absoluteValue, z.absoluteValue)
val Vec2.abs: Vec2
    inline get() = vec2(x.absoluteValue, y.absoluteValue)

val Vec4.wNormalizd: Vec4
    inline get() = this / w

fun Vec3.max(v: Vec3): Vec3 = vec3(max(x, v.x), max(y, v.y), max(z, v.z))

/////////
// Map to external
@PublishedApi
internal inline fun asColorComponent(x: Float) = (x.coerceIn(0f, 1f) * 255).toInt()
inline fun Vec4.toColor(
    r: Float = this.r,
    g: Float = this.g,
    b: Float = this.b,
    a: Float = this.a,
) = Color.argb(asColorComponent(a), asColorComponent(r), asColorComponent(g), asColorComponent(b))
