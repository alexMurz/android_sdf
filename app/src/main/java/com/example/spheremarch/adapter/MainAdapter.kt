package com.example.spheremarch.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spheremarch.adapter.items.*
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager

private object DiffCallback : DiffUtil.ItemCallback<MainAdapterItem>() {
    override fun areItemsTheSame(oldItem: MainAdapterItem, newItem: MainAdapterItem): Boolean =
        oldItem.isSameItem(newItem)

    override fun areContentsTheSame(oldItem: MainAdapterItem, newItem: MainAdapterItem): Boolean =
        oldItem.isSameContent(newItem)
}

val MainAdapterDelegates = listOf(
    MainAdapterScrollerDelegate,
    MainAdapterSeparatorDelegate,
    MainAdapterHorizontalDelegate,
    MainAdapterTextDelegate,
    MainAdapterButtonDelegate,
)

fun createMainAdapterDelegateManager() = AdapterDelegatesManager<List<MainAdapterItem>>().apply {
    MainAdapterDelegates.forEach(this::addDelegate)
}

class MainAdapter : ListAdapter<MainAdapterItem, RecyclerView.ViewHolder>(DiffCallback) {
    private val delegateManager = createMainAdapterDelegateManager()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = currentList[position].id.toLong()

    override fun getItemViewType(position: Int): Int = delegateManager.getItemViewType(
        currentList,
        position
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateManager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int): Unit =
        delegateManager.onBindViewHolder(currentList, position, holder)
}