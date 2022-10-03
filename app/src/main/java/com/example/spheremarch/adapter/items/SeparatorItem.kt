package com.example.spheremarch.adapter.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.ext.dp
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class SeparatorItem(
    override val id: Int,
) : MainAdapterItem

object MainAdapterSeparatorDelegate : AdapterDelegate<List<MainAdapterItem>>() {
    private val separatorHeight = 1.dp
    private val separatorPadding = 1.dp

    override fun isForViewType(items: List<MainAdapterItem>, position: Int): Boolean =
        items[position] is SeparatorItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        SeparatorViewHolder(createView(parent))

    override fun onBindViewHolder(
        items: List<MainAdapterItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
    }

    private fun createView(parent: ViewGroup): View {
        return FrameLayout(parent.context).apply {
            setPadding(HORIZONTAL_PADDING, separatorPadding, HORIZONTAL_PADDING, separatorPadding)
            addView(View(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    separatorHeight
                )
                background = ColorDrawable(Color.BLACK)
            })
        }
    }
}

private class SeparatorViewHolder(view: View) : RecyclerView.ViewHolder(view)
