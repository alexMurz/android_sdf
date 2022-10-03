package com.example.spheremarch.adapter.items

import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.adapter.Stateful
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class TextItem(
    override val id: Int,
    val text: Stateful<String>,
) : MainAdapterItem

object MainAdapterTextDelegate : AdapterDelegate<List<MainAdapterItem>>() {
    override fun isForViewType(items: List<MainAdapterItem>, position: Int): Boolean =
        items[position] is TextItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        TextItemViewHolder(
            view = createView(parent)
        )

    override fun onBindViewHolder(
        items: List<MainAdapterItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as TextItemViewHolder).bind(items[position] as TextItem)
    }

    private fun createView(parent: ViewGroup): TextView = AppCompatTextView(parent.context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
        setTextColor(Color.BLACK)
        setPadding(HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING)
    }
}

private class TextItemViewHolder(
    private val view: TextView,
) : RecyclerView.ViewHolder(view) {
    private val scope = MainScope()

    fun bind(item: TextItem) {
        scope.coroutineContext.cancelChildren()
        item.text.valueState
            .onEach(view::setText)
            .launchIn(scope)
    }
}
