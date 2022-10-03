package com.example.spheremarch.marcher

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.get
import com.example.spheremarch.scene.*
import java.util.concurrent.atomic.AtomicBoolean

private val nativeReady = AtomicBoolean(false)

private typealias Ptr = Long

class MarcherComputeNative(override val resolution: Int) : MarcherCompute {
    init {
        if (!nativeReady.get()) synchronized(javaClass) {
            if (nativeReady.compareAndSet(false, true)) {
                System.loadLibrary("nativesm")
            }
        }
    }

    private val ptr = create(resolution)

    override fun applyConfig(marcherConfig: MarcherConfig) {
        applyConfig(ptr, marcherConfig.nativeBuild())
    }

    override fun update(bitmap: Bitmap): Unit = run(ptr, bitmap)

    private external fun create(resolution: Int): Ptr

    private external fun applyConfig(ptr: Ptr, config: Ptr)

    private external fun run(ptr: Ptr, bitmap: Bitmap)

    private external fun drop(ptr: Ptr)
}

private fun MarcherConfig.nativeBuild(): Ptr = with(ConfigBuilder) {
    val ptr = create()
    try {
        setEyePos(ptr, viewer.eyePosition.x, viewer.eyePosition.y, viewer.eyePosition.z)
        setEyeAt(ptr, viewer.eyeLookAt.x, viewer.eyeLookAt.y, viewer.eyeLookAt.z)
        setEyeUp(ptr, viewer.eyeUp.x, viewer.eyeUp.y, viewer.eyeUp.z)
        setMaxDrawDistance(ptr, ray.maxTravelDist)

        for (node in scene.nodes) {
            addNode(ptr, node.nativeBuild())
        }
    } catch (e: Exception) {
        destroyUnused(ptr)
        throw e
    }

    ptr
}

private fun Node.nativeBuild(): Ptr = with(NodeBuilder) {
    val ptr = when (shape) {
        is SphereShape -> createSphere(shape.radius)
        is BoxShape -> createBox(shape.halfSize.x, shape.halfSize.y, shape.halfSize.z)
    }

    try {
        when (op) {
            Op.Union -> opUnion(ptr)
            Op.Subtract -> opSubtract(ptr)
            Op.Intersect -> opIntersect(ptr)
            is Op.UnionSmooth -> opSmoothUnion(ptr, op.pow)
            is Op.SubtractSmooth -> opSmoothSubtract(ptr, op.pow)
            is Op.IntersectSmooth -> opSmoothIntersect(ptr, op.pow)
            else -> throw UnsupportedOperationException("Other operations currently not supported by native compute")
        }

        for (m in modifiers) {
            when (m) {
                is OpModifier.Translate -> addModTranslate(ptr, m.offset.x, m.offset.y, m.offset.z)
                is OpModifier.Scale -> addModScale(ptr, m.scale.x, m.scale.y, m.scale.z)
                else -> throw UnsupportedOperationException("Custom modifiers not supported by native compute")
            }
        }

        matDiffuse(ptr, mat.color.x, mat.color.y, mat.color.z)
    } catch (e: Exception) {
        destroyUnused(ptr)
        throw e
    }

    return ptr
}

object NodeBuilder {
    external fun createSphere(v: Float): Ptr
    external fun createBox(x: Float, y: Float, z: Float): Ptr

    external fun destroyUnused(ptr: Ptr)

    external fun addModTranslate(ptr: Ptr, x: Float, y: Float, z: Float)
    external fun addModScale(ptr: Ptr, x: Float, y: Float, z: Float)

    external fun opUnion(ptr: Ptr)
    external fun opSubtract(ptr: Ptr)
    external fun opIntersect(ptr: Ptr)

    external fun opSmoothUnion(ptr: Ptr, arg: Float)
    external fun opSmoothSubtract(ptr: Ptr, arg: Float)
    external fun opSmoothIntersect(ptr: Ptr, arg: Float)

    external fun matDiffuse(ptr: Ptr, r: Float, g: Float, b: Float)
}

object ConfigBuilder {
    external fun create(): Ptr
    external fun destroyUnused(ptr: Ptr)

    external fun setEyePos(ptr: Ptr, x: Float, y: Float, z: Float)
    external fun setEyeAt(ptr: Ptr, x: Float, y: Float, z: Float)
    external fun setEyeUp(ptr: Ptr, x: Float, y: Float, z: Float)
    external fun setSpread(ptr: Ptr, x: Float)
    external fun setMaxDrawDistance(ptr: Ptr, x: Float)

    external fun addNode(ptr: Ptr, node: Ptr)
}

