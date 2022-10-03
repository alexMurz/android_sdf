package com.example.spheremarch.adapter.items

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.ext.add
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlin.properties.Delegates

data class ScrollerItem(
    override val id: Int,
    val title: String,
    val valueTo: Int = 100,
    var currentValue: Int = 0,
    val valueVisualMapper: (Int) -> String = { it.toString() },
    val onValueChanged: (Int) -> Unit,
) : MainAdapterItem

object MainAdapterScrollerDelegate : AdapterDelegate<List<MainAdapterItem>>() {
    override fun isForViewType(items: List<MainAdapterItem>, position: Int): Boolean =
        items[position] is ScrollerItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ScrollItemViewHolder(
            view = ScrollItemView(parent.context)
        )

    override fun onBindViewHolder(
        items: List<MainAdapterItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ScrollItemViewHolder).bind(items[position] as ScrollerItem)
    }
}

private class ScrollItemView(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrSet, defStyleAttr) {
    init {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setPadding(
            HORIZONTAL_PADDING,
            VERTICAL_PADDING,
            HORIZONTAL_PADDING,
            VERTICAL_PADDING,
        )
    }

    private val titleFrame = add(FrameLayout(context)) {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    val title = titleFrame.add(AppCompatTextView(context)) {
        gravity = Gravity.START
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
    }

    val valueView = titleFrame.add(AppCompatTextView(context)) {
        gravity = Gravity.END
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
    }

    val seekBar = add(AppCompatSeekBar(context))
}


private class ScrollItemViewHolder(
    private val view: ScrollItemView,
) : RecyclerView.ViewHolder(view), SeekBar.OnSeekBarChangeListener {
    private lateinit var item: ScrollerItem

    private var currentValue by Delegates.observable(-1) { _, old, new ->
        if (old != new) {
            item.currentValue = new
            item.onValueChanged(new)
            view.valueView.text = item.valueVisualMapper(new)
        }
    }

    fun bind(item: ScrollerItem) {
        this.item = item
        currentValue = item.currentValue
        with(view) {
            title.text = item.title
            seekBar.max = item.valueTo
            seekBar.progress = item.currentValue
            seekBar.setOnSeekBarChangeListener(this@ScrollItemViewHolder)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        currentValue = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}
