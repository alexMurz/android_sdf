package com.example.spheremarch.adapter.items

import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.adapter.Stateful
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ButtonItem(
    override val id: Int,
    val title: Stateful<String>,
    val handleClick: () -> Unit
) : MainAdapterItem

object MainAdapterButtonDelegate : AdapterDelegate<List<MainAdapterItem>>() {
    override fun isForViewType(items: List<MainAdapterItem>, position: Int): Boolean =
        items[position] is ButtonItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ButtonItemViewHolder(
            view = createView(parent)
        )

    override fun onBindViewHolder(
        items: List<MainAdapterItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ButtonItemViewHolder).bind(items[position] as ButtonItem)
    }

    private fun createView(parent: ViewGroup): Button = AppCompatButton(parent.context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
        setTextColor(Color.BLACK)
        setPadding(HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING)
    }
}

private class ButtonItemViewHolder(
    private val view: Button,
) : RecyclerView.ViewHolder(view) {
    private val scope = MainScope()

    fun bind(item: ButtonItem) {
        scope.coroutineContext.cancelChildren()
        item.title.valueState.onEach(view::setText).launchIn(scope)
        view.setOnClickListener { item.handleClick() }
    }
}
