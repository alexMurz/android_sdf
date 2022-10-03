package com.example.spheremarch

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.ext.add
import com.example.spheremarch.ext.configureConstraints

private const val VIEWPORT_ID = 1
private const val CTRL_RECYCLER_ID = 2

class MainActivityView(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrSet, defStyleAttr) {

    init {
        background = ColorDrawable(Color.WHITE)
    }

    val marchView = add(SphereMarcherView(context)) {
        configureConstraints {
            leftToLeft = PARENT_ID
            topToTop = PARENT_ID
            rightToRight = PARENT_ID
            dimensionRatio = "1:1"
        }
        id = VIEWPORT_ID
        background = ColorDrawable(Color.BLACK)
    }


    val controlRecycler = add(RecyclerView(context)) {
        configureConstraints {
            leftToLeft = PARENT_ID
            topToBottom = VIEWPORT_ID
            rightToRight = PARENT_ID
            bottomToBottom = PARENT_ID
        }
        id = CTRL_RECYCLER_ID
        layoutManager = LinearLayoutManager(context)
    }
}