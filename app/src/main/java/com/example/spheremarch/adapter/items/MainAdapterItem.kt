package com.example.spheremarch.adapter.items

import com.example.spheremarch.ext.dp

internal const val TEXT_SIZE = 18f
internal val VERTICAL_PADDING = 12.dp
internal val HORIZONTAL_PADDING = 12.dp


sealed interface MainAdapterItem {
    val id: Int

    fun isSameContent(other: MainAdapterItem): Boolean = this == other

    fun isSameItem(other: MainAdapterItem): Boolean = id == other.id
}
