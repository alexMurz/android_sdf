package com.example.spheremarch.ext

import android.content.res.Resources
import kotlin.math.roundToInt

val Int.dp: Int
    inline get() = toFloat().dp

val Float.dp: Int
    inline get() = (Resources.getSystem().displayMetrics.density * this).roundToInt()
