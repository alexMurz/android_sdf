package com.example.spheremarch.scene

import com.example.spheremarch.math.Vec3
import com.example.spheremarch.math.div
import com.example.spheremarch.math.minus

// Generic modifier types for easy matching
enum class OpModifierType {
    Custom, Translate, Scale
}

interface OpModifier {
    val type: OpModifierType
        get() = OpModifierType.Custom

    fun apply(o: Vec3, d1: Float): Vec3?

    data class Translate(val offset: Vec3) : OpModifier {
        override val type: OpModifierType
            get() = OpModifierType.Translate

        override fun apply(o: Vec3, d1: Float): Vec3 = o - offset
    }

    data class Scale(val scale: Vec3) : OpModifier {
        override val type: OpModifierType
            get() = OpModifierType.Scale

        override fun apply(o: Vec3, d1: Float): Vec3 = o / scale
    }
}

