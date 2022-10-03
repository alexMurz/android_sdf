@file:Suppress("NOTHING_TO_INLINE")

package com.example.spheremarch.marcher

import com.example.spheremarch.math.Vec3
import com.example.spheremarch.scene.Object
import com.example.spheremarch.scene.Scene


data class MarcherViewer(
    val eyePosition: Vec3,
    val eyeLookAt: Vec3,
    val eyeUp: Vec3,
)

data class MarcherRay(
    val stopDist: Float,
    val maxTravelDist: Float,
    val maxHopCount: Int,
)

data class MarcherConfig(
    val viewer: MarcherViewer,
    val ray: MarcherRay,
    val scene: Scene,
)
