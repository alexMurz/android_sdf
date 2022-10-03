package com.example.spheremarch.scene

import com.example.spheremarch.math.*

private val EPS = 1e-4f
private val E = vec2(1f, -1f) * EPS
private val xyy = E.xyy
private val yyx = E.yyx
private val yxy = E.yxy
private val xxx = E.xxx

data class Scene(
    val nodes: List<Node>,
) {
    fun mapDistance(o: Vec3, maxDist: Float): Pair<Float, Vec4> {
        var d1 = maxDist
        var c1 = vec4Zero
        for (node in nodes) {
            val (d2, c2) = node.apply(d1, c1, o)
            d1 = d2
            c1 = c2
        }
        return d1 to c1
    }

    fun findNormal(o: Vec3): Vec3 {
        val r = vec3Zero
        r.plusAssign(xyy * mapDistance(o + xyy, Float.MAX_VALUE).first)
        r.plusAssign(yyx * mapDistance(o + yyx, Float.MAX_VALUE).first)
        r.plusAssign(yxy * mapDistance(o + yxy, Float.MAX_VALUE).first)
        r.plusAssign(xxx * mapDistance(o + xxx, Float.MAX_VALUE).first)
        return r.normalizeInPlace()
    }
}
