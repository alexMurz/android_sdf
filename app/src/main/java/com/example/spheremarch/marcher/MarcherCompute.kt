package com.example.spheremarch.marcher

import android.graphics.Bitmap

interface MarcherCompute {
    val resolution: Int

    fun applyConfig(marcherConfig: MarcherConfig)

    fun update(bitmap: Bitmap)
}