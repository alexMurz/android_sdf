@file:Suppress("NOTHING_TO_INLINE")

package com.example.spheremarch.math

data class Mat4(
    val arr: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mat4

        if (!arr.contentEquals(other.arr)) return false

        return true
    }

    override fun toString(): String {
        return "[$r1, $r2, $r3, $r4]"
    }

    override fun hashCode(): Int {
        return arr.contentHashCode()
    }

    companion object {
        val Identity = mat4(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }
}

// Constructors
inline fun mat4(arr: FloatArray): Mat4 {
    assert(arr.size == 16)
    return Mat4(arr = arr)
}

inline fun mat4(
    r1c1: Float, r1c2: Float, r1c3: Float, r1c4: Float,
    r2c1: Float, r2c2: Float, r2c3: Float, r2c4: Float,
    r3c1: Float, r3c2: Float, r3c3: Float, r3c4: Float,
    r4c1: Float, r4c2: Float, r4c3: Float, r4c4: Float,
) = mat4(
    floatArrayOf(
        r1c1, r1c2, r1c3, r1c4,
        r2c1, r2c2, r2c3, r2c4,
        r3c1, r3c2, r3c3, r3c4,
        r4c1, r4c2, r4c3, r4c4,
    )
)

inline fun mat4(r1: Vec4, r2: Vec4, r3: Vec4, r4: Vec4) = mat4(
    r1.x, r1.y, r1.z, r1.w,
    r2.x, r2.y, r2.z, r2.w,
    r3.x, r3.y, r3.z, r3.w,
    r4.x, r4.y, r4.z, r4.w,
)

inline fun mat4Identity() = Mat4.Identity

inline fun mat4Scale(scale: Float) = mat4(
    scale, 0f, 0f, 0f,
    0f, scale, 0f, 0f,
    0f, 0f, scale, 0f,
    0f, 0f, 0f, 1f
)

inline fun mat4ScaleNonUniform(x: Float, y: Float, z: Float) = mat4(
    x, 0f, 0f, 0f,
    0f, y, 0f, 0f,
    0f, 0f, z, 0f,
    0f, 0f, 0f, 1f
)

inline fun mat4Translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) = mat4(
    1f, 0f, 0f, x,
    0f, 1f, 0f, y,
    0f, 0f, 1f, z,
    0f, 0f, 0f, 1f,
)

// Cells
val Mat4.r1c1: Float
    inline get() = arr[0]
val Mat4.r1c2: Float
    inline get() = arr[1]
val Mat4.r1c3: Float
    inline get() = arr[2]
val Mat4.r1c4: Float
    inline get() = arr[3]
val Mat4.r2c1: Float
    inline get() = arr[4]
val Mat4.r2c2: Float
    inline get() = arr[5]
val Mat4.r2c3: Float
    inline get() = arr[6]
val Mat4.r2c4: Float
    inline get() = arr[7]
val Mat4.r3c1: Float
    inline get() = arr[8]
val Mat4.r3c2: Float
    inline get() = arr[9]
val Mat4.r3c3: Float
    inline get() = arr[10]
val Mat4.r3c4: Float
    inline get() = arr[11]
val Mat4.r4c1: Float
    inline get() = arr[12]
val Mat4.r4c2: Float
    inline get() = arr[13]
val Mat4.r4c3: Float
    inline get() = arr[14]
val Mat4.r4c4: Float
    inline get() = arr[15]

fun Mat4.cell(x: Int, y: Int): Float {
    assert(x in 0..3)
    assert(y in 0..3)
    return arr[x + y * 4]
}

// Rows
val Mat4.r1: Vec4
    inline get() = vec4(r1c1, r1c2, r1c3, r1c4)

val Mat4.r2: Vec4
    inline get() = vec4(r2c1, r2c2, r2c3, r2c4)

val Mat4.r3: Vec4
    inline get() = vec4(r3c1, r3c2, r3c3, r3c4)

val Mat4.r4: Vec4
    inline get() = vec4(r4c1, r4c2, r4c3, r4c4)

// Cols
val Mat4.c1: Vec4
    inline get() = vec4(r1c1, r2c1, r3c1, r4c1)

val Mat4.c2: Vec4
    inline get() = vec4(r1c2, r2c2, r3c2, r4c2)

val Mat4.c3: Vec4
    inline get() = vec4(r1c3, r2c3, r3c3, r4c3)

val Mat4.c4: Vec4
    inline get() = vec4(r1c4, r2c4, r3c4, r4c4)

// Math
inline operator fun Mat4.times(v: Mat4): Mat4 {
    val arr = FloatArray(16) { 0f }
    for (i in 0 until 16) {
        val x = i % 4
        val y = i / 4
        var r = 0f
        for (j in 0 until 4) {
            r += cell(j, y) * v.cell(x, j)
        }
        arr[i] = r
    }
    return mat4(arr)
}

// Mat * Column vector
inline operator fun Mat4.times(v: Vec4): Vec4 = vec4(
    r1.dot(v),
    r2.dot(v),
    r3.dot(v),
    r4.dot(v),
)

val Mat4.transpose: Mat4
    inline get() = mat4(c1, c2, c3, c4)
