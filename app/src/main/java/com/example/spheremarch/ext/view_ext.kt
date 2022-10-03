@file:Suppress("NOTHING_TO_INLINE")

package com.example.spheremarch.ext

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

inline fun <V : View> ViewGroup.add(view: V): V {
    addView(view)
    return view
}

inline fun <V : View> ViewGroup.add(view: V, apply: V.() -> Unit): V {
    apply(view)
    addView(view)
    return view
}

inline fun View.configureConstraints(
    width: Int = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    height: Int = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    apply: ConstraintLayout.LayoutParams.() -> Unit
) {
    layoutParams = ConstraintLayout.LayoutParams(width, height).apply(apply)
}
