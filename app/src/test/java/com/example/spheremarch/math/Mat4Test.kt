package com.example.spheremarch.math

import org.junit.Assert.assertEquals
import org.junit.Test

class Mat4Test {
    @Test
    fun shouldMat4multiply() {
        val a = mat4(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        val b = mat4(
            13f, 14f, 15f, 16f,
            9f, 10f, 11f, 12f,
            5f, 6f, 7f, 8f,
            1f, 2f, 3f, 4f,
        )
        val c = a * b
        assertEquals(
            c,
            mat4(
                50f, 60f, 70f, 80f,
                162f, 188f, 214f, 240f,
                274f, 316f, 358f, 400f,
                386f, 444f, 502f, 560f,
            )
        )
    }

    @Test
    fun shouldCreateScaleAndRotate() {
        val scale = mat4Scale(2f)
        val trans = mat4Translate(x = 10f, y = 20f, z = 30f)
        val r = trans * scale
        assertEquals(
            mat4(
                2f, 0f, 0f, 10f,
                0f, 2f, 0f, 20f,
                0f, 0f, 2f, 30f,
                0f, 0f, 0f, 1f
            ),
            r,
        )
    }

    @Test
    fun shouldTranslateVector() {
        val matrix = mat4Translate(x = 1f, y = 2f)
        val vector = vec4(1f, 1f, 0f, 1f)
        val t = matrix * vector
        println("Matrix: $matrix")
        println("Vector: $vector")
        println("Result: $t")
        assertEquals(
            vec4(2f, 3f, 0f, 1f),
            t,
        )
    }
}