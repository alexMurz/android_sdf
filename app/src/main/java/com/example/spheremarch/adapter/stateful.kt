package com.example.spheremarch.adapter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

interface Stateful<T> {
    val valueState: StateFlow<T>
    val value: T
        get() = valueState.value

}

interface MutableStateful<T> : Stateful<T> {
    override var value: T
}

operator fun <T> Stateful<T>.getValue(t: Any?, property: KProperty<*>): T = value

operator fun <T> MutableStateful<T>.setValue(t: Any?, property: KProperty<*>, v: T) {
    value = v
}


fun <T> MutableStateFlow<T>.stateful(): MutableStateful<T> = StatefulImpl(this)

fun <T> T.fixed(): Stateful<T> = FixedImpl(this)

private class FixedImpl<T>(value: T) : Stateful<T> by MutableStateFlow(value).stateful()

private class StatefulImpl<T>(private val flow: MutableStateFlow<T>) : MutableStateful<T> {
    override val valueState: StateFlow<T>
        get() = flow
    override var value: T
        get() = flow.value
        set(value) {
            flow.value = value
        }
}
