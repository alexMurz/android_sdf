package com.example.spheremarch.scene

import com.example.spheremarch.math.*

interface Op {
    fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4>

    object Union : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> =
            if (d1 < d2) d1 to c1 else d2 to c2
    }

    object Subtract : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> =
            if (d1 > -d2) d1 to c1 else d1 to c2
    }

    object Intersect : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> =
            if (d1 > d2) d1 to c1 else d2 to c1
    }

    data class UnionSmooth(val pow: Float) : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> {
            val h = (0.5f + 0.5f * (d2 - d1) / pow).coerceIn(0f, 1f)
            val d = (d2 + (d1 - d2) * h) - pow * h * (1.0f - h)
            val c = c2 + (c1 - c2) * h
            return d to c
        }
    }

    data class SubtractSmooth(val pow: Float) : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> {
            val h = (0.5f - 0.5f * (d2 + d1) / pow).coerceIn(0f, 1f)
            val d = (d2 + (-d1 - d2) * h) + pow * h * (1.0f - h)
            val c = c2 + (-c1 - c2) * h
            return d to c
        }
    }

    data class IntersectSmooth(val pow: Float) : Op {
        override fun apply(d1: Float, c1: Vec4, d2: Float, c2: Vec4): Pair<Float, Vec4> {
            val h = (0.5f - 0.5f * (d2 - d1) / pow).coerceIn(0f, 1f)
            val d = (d2 + (d1 - d2) * h) + pow * h * (1.0f - h)
            val c = c2 + (c1 - c2) * h
            return d to c
        }
    }
}

class Node(
    val shape: Shape,
    val mat: Material,
    val modifiers: List<OpModifier>,
    val op: Op
) {
    fun resolveOrigin(o: Vec3, d1: Float): Vec3? {
        var origin: Vec3 = o
        for (m in modifiers) {
            origin = m.apply(origin, d1) ?: return null
        }
        return origin
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun apply(d1: Float, c1: Vec4, o: Vec3): Pair<Float, Vec4> {
        val origin = resolveOrigin(o, d1) ?: return d1 to c1
        val d2 = shape.distanceTo(origin)
        val c2 = mat.color
        return op.apply(d1, c1, d2, c2)
    }
}
