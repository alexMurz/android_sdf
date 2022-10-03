package com.example.spheremarch.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}
