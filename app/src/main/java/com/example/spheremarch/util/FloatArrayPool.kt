package com.example.spheremarch.util

class FloatArrayPool : Pool<FloatArray> {
    var bufferSize: Int = 0
        set(v) {
            require(v >= 0)
            field = v
        }

    private val buffers = mutableListOf<FloatArray>()

    override fun obtain(): FloatArray {
        var arr = buffers.removeLastOrNull()
        val size = bufferSize

        // Pop until find array with required size
        while (arr != null && arr.size < size) {
            arr = buffers.removeLastOrNull()
        }

        return arr ?: FloatArray(size)
    }

    override fun release(value: FloatArray) {
        if (value.size >= bufferSize) buffers.add(value)
    }
}
