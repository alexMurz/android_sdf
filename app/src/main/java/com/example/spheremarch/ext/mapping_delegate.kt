package com.example.spheremarch.ext

import com.example.spheremarch.adapter.MutableStateful
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface BiMapper<T, R> {
    fun map(value: T): R

    fun unmap(value: R): T
}

class MappingDelegate<T, R>(
    private val src: KMutableProperty0<T>,
    private val map: (T) -> R,
    private val unmap: (R) -> T,
) : ReadWriteProperty<Any?, R> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): R {
        return map(src.get())
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
        src.set(unmap(value))
    }
}

fun <T, R> mappingDelegate(
    prop: KMutableProperty0<T>,
    map: (T) -> R,
    unmap: (R) -> T,
): ReadWriteProperty<Any?, R> = MappingDelegate(prop, map, unmap)

fun <T, R> mappingDelegate(
    prop: KMutableProperty0<T>,
    mapper: BiMapper<T, R>
): ReadWriteProperty<Any?, R> = MappingDelegate(prop, mapper::map, mapper::unmap)

fun <T, R> MutableStateful<T>.mappingDelegate(
    map: (T) -> R,
    unmap: (R) -> T,
): ReadWriteProperty<Any?, R> = mappingDelegate(this::value, map, unmap)

fun <T, R> MutableStateful<T>.mappingDelegate(
    mapper: BiMapper<T, R>
): ReadWriteProperty<Any?, R> = mappingDelegate(this::value, mapper::map, mapper::unmap)
