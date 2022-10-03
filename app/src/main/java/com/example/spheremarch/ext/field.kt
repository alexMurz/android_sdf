package com.example.spheremarch.ext

import kotlin.reflect.KMutableProperty0


fun <T> KMutableProperty0<T?>.take(): T? = get().also { set(null) }
