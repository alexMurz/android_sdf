package com.example.spheremarch.adapter

import com.example.spheremarch.adapter.items.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

interface UIDsl {
    fun scroller(
        title: String,
        defaultValue: Float = 0f,
        min: Float = 0f,
        max: Float = 1f,
        step: Float = 0.1f,
        onValueChanged: (Float) -> Unit
    )

    fun separator()

    fun horizontal(builder: UIDsl.() -> Unit)

    fun text(text: Stateful<String>)

    fun text(initial: String = ""): MutableStateful<String> {
        return MutableStateFlow(initial).stateful().also {
            text(it)
        }
    }

    fun button(title: Stateful<String>, onClick: () -> Unit)
}

class UIDslHost {
    private val mId = AtomicInteger(0)

    internal fun acquireItemId() = mId.getAndIncrement()
}

private class DslBuildCollector(
    val host: UIDslHost,
    val items: MutableList<MainAdapterItem> = mutableListOf(),
) : UIDsl {
    override fun scroller(
        title: String,
        defaultValue: Float,
        min: Float,
        max: Float,
        step: Float,
        onValueChanged: (Float) -> Unit
    ) {
        val intScale = 1f / step

        val lMin = min(min, max)
        val lMax = max(min, max)
        val dist = ((lMax - lMin) * intScale).toInt()

        val toScaled = { it: Float -> ((it - lMin) * intScale).toInt() }
        val fromScaled = { it: Int -> (it * step) + lMin }

        items.add(ScrollerItem(
            id = host.acquireItemId(),
            title = title,
            currentValue = toScaled(defaultValue.coerceIn(lMin, lMax)),
            valueTo = dist,
            valueVisualMapper = { String.format("%.1f", fromScaled(it)) },
            onValueChanged = { onValueChanged(fromScaled(it)) }
        ))
    }


    override fun separator() {
        items.add(SeparatorItem(host.acquireItemId()))
    }

    override fun horizontal(builder: UIDsl.() -> Unit) {
        val collector = DslBuildCollector(host)
        builder(collector)
        items.add(
            HorizontalItem(
                id = host.acquireItemId(),
                items = collector.items
            )
        )
    }

    override fun text(text: Stateful<String>) {
        items.add(
            TextItem(
                id = host.acquireItemId(),
                text = text,
            )
        )
    }

    override fun button(title: Stateful<String>, onClick: () -> Unit) {
        items.add(
            ButtonItem(
                id = host.acquireItemId(),
                title = title,
                handleClick = onClick,
            )
        )
    }
}

fun UIDslHost.buildUi(action: UIDsl.() -> Unit): List<MainAdapterItem> {
    val rootCollector = DslBuildCollector(this)
    action(rootCollector)
    return rootCollector.items
}
