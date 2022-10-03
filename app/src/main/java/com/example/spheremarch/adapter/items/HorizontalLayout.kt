package com.example.spheremarch.adapter.items

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.adapter.createMainAdapterDelegateManager
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class HorizontalItem(
    override val id: Int,
    val items: List<MainAdapterItem>,
) : MainAdapterItem

object MainAdapterHorizontalDelegate : AdapterDelegate<List<MainAdapterItem>>() {
    override fun isForViewType(items: List<MainAdapterItem>, position: Int): Boolean =
        items[position] is HorizontalItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        HorizontalLayoutViewHolder(createView(parent))

    override fun onBindViewHolder(
        items: List<MainAdapterItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as HorizontalLayoutViewHolder).bind(items[position] as HorizontalItem)
    }

    private fun createView(parent: ViewGroup): LinearLayout {
        return LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200
            )
            orientation = LinearLayout.HORIZONTAL
        }
    }
}

private class HorizontalLayoutViewHolder(
    private val layout: LinearLayout,
) : RecyclerView.ViewHolder(layout) {
    val manager = createMainAdapterDelegateManager()

    private lateinit var item: HorizontalItem

    fun bind(item: HorizontalItem) {
        this.item = item

        layout.removeAllViews()
        for (i in item.items.indices) {
            val type = manager.getItemViewType(item.items, i)
            val vh = manager.onCreateViewHolder(layout, type)
            vh.setItemViewType(type)
            manager.onBindViewHolder(item.items, i, vh)

            val view = vh.itemView
            view.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
            layout.addView(view)
        }
    }

    private fun RecyclerView.ViewHolder.setItemViewType(type: Int) {
        mItemViewTypeProp.set(this, type)
    }

    companion object {
        private val mItemViewTypeProp = RecyclerView.ViewHolder::class.java.declaredFields
            .find { it.name == "mItemViewType" }!!
            .also {
                it.isAccessible = true
            }
    }
}
