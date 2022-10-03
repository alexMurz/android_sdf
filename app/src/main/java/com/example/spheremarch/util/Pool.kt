package com.example.spheremarch.util

interface Pool<T> {
    fun obtain(): T
    fun release(value: T)
}

inline fun <T, R> Pool<T>.use1(action: (T) -> R): R {
    val v = obtain()
    return try {
        action(v)
    } finally {
        release(v)
    }
}

inline fun <T, R> Pool<T>.use2(action: (T, T) -> R): R {
    val v1 = obtain()
    val v2 = obtain()
    return try {
        action(v1, v2)
    } finally {
        release(v1)
        release(v2)
    }
}
