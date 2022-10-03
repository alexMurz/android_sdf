package com.example.spheremarch

import androidx.lifecycle.ViewModel
import com.example.spheremarch.adapter.UIDslHost
import com.example.spheremarch.adapter.buildUi
import com.example.spheremarch.adapter.items.MainAdapterItem
import com.example.spheremarch.marcher.MarcherConfig
import com.example.spheremarch.marcher.MarcherRay
import com.example.spheremarch.marcher.MarcherViewer
import com.example.spheremarch.math.Vec3
import com.example.spheremarch.math.vec3
import com.example.spheremarch.math.vec4
import com.example.spheremarch.scene.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty


interface MainViewModel {
    val controlListState: Flow<List<MainAdapterItem>>

    val marcherConfigState: Flow<MarcherConfig>
}

class MainViewModelImpl : ViewModel(), MainViewModel {

    private var eyePosition: Vec3 by invalidating(vec3(0f, 0f, -10f))
    private var eyeUp: Vec3 by invalidating(vec3(0f, 1f, 0f))
    private var eyeLookAt: Vec3 by invalidating(vec3(0f, 0f, 0f))
    private var stopDist: Float by invalidating(0.01f)
    private var arg: Float by invalidating(0f)

    private var offset1 by invalidating(1.5f)
    private var offset2 by invalidating(-1.5f)

    private val uiHost = UIDslHost()

    private val options = uiHost.buildUi {
        horizontal {
            scroller(
                title = "Eye X",
                min = -3.0f,
                max = 3.0f,
                onValueChanged = { eyePosition = eyePosition.copy(x = it) }
            )
            scroller(
                title = "Eye Y",
                min = -3.0f,
                max = 3.0f,
                onValueChanged = { eyePosition = eyePosition.copy(y = it) }
            )
            scroller(
                title = "Eye Z",
                defaultValue = 3.0f,
                min = 1.0f,
                max = 3.0f,
                onValueChanged = { eyePosition = eyePosition.copy(z = it) }
            )
        }

        horizontal {
            scroller(
                title = "Stop",
                min = 1f,
                max = 1000f,
                step = 1f,
                onValueChanged = { stopDist = it / 1000f }
            )
            scroller(
                title = "Arg",
                min = 0f,
                max = 200f,
                step = 1f,
                onValueChanged = { arg = it / 100f }
            )
        }

        separator()

        horizontal {
            scroller(
                title = "Left offset",
                defaultValue = -1f,
                min = -3f,
                max = 0f,
                onValueChanged = {
                    offset1 = it
                }
            )

            scroller(
                title = "Right offset",
                defaultValue = 1f,
                min = 0f,
                max = 3f,
                onValueChanged = {
                    offset2 = it
                }
            )
        }
    }

    private val mConfig = MutableStateFlow(buildConfig())

    private val mOptionListState = MutableStateFlow(options)

    private fun invalidateConfig() {
        mConfig.value = buildConfig()
    }

    private fun buildConfig() = MarcherConfig(
        viewer = MarcherViewer(
            eyePosition = eyePosition,
            eyeLookAt = eyeLookAt,
            eyeUp = eyeUp,
        ),
        ray = MarcherRay(
            stopDist = stopDist,
            maxTravelDist = 100f,
            maxHopCount = 100,
        ),
        scene = Scene(
            nodes = listOf(
                Node(
                    shape = BoxShape(halfSize = vec3(.5f, 1f, 1f)),
                    mat = Material(color = vec4(0f, 1f, 0f, 1f)),
                    modifiers = listOf(
                        OpModifier.Translate(vec3(offset2, 0f, 0f)),
                    ),
                    op = Op.Union, // Intersect,
                ),
                Node(
                    shape = BoxShape(halfSize = vec3(0.7f, 0.2f, 1.0f)),
                    mat = Material(color = vec4(0f, 0f, 1f, 1f)),
                    modifiers = listOf(
                        OpModifier.Translate(vec3(offset1, 0f, 0f)),
                    ),
                    op = Op.UnionSmooth(arg), // (arg),
                ),
                Node(
                    shape = SphereShape(radius = 0.5f),
                    mat = Material(color = vec4(1f, 0f, 0f, 1f)),
                    modifiers = listOf(
                        OpModifier.Scale(vec3(1f, 1f, 2f))
                    ),
                    op = Op.Subtract,
                ),
            )
        ),
    )


    override val controlListState: Flow<List<MainAdapterItem>>
        get() = mOptionListState

    override val marcherConfigState: Flow<MarcherConfig>
        get() = mConfig

    // ### UTIL ###
    private fun <T> invalidating(default: T): ReadWriteProperty<Any?, T> {
        return Delegates.observable(
            initialValue = default,
            onChange = { _, old, new ->
                if (old != new) invalidateConfig()
            }
        )
    }
}
