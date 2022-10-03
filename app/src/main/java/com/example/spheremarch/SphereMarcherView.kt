package com.example.spheremarch

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.createBitmap
import com.example.doomflame.swapchain.SwapchainImpl
import com.example.spheremarch.ext.take
import com.example.spheremarch.marcher.MarcherCompute
import com.example.spheremarch.math.WindowMean
import com.example.spheremarch.swapchain.Swapchain
import com.example.spheremarch.swapchain.update
import kotlin.system.measureNanoTime

private class Updater(
    val marcherCompute: MarcherCompute,
    val swapchain: Swapchain<Bitmap>,
    val mean: WindowMean? = null,
) : Thread("MarcherUpdater") {
    override fun run() {
        while (!isInterrupted) {
            swapchain.update {
                val ns = measureNanoTime {
                    marcherCompute.update(it)
                }
                mean?.put(ns / 1e9f)
            }
        }
    }
}

class SphereMarcherView(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {

    private val upsMean = WindowMean(100)
    private var capturedBitmap: Bitmap? = null
    private val srcRect: Rect = Rect()
    private val dstRect: Rect = Rect()

    private var updater: Updater? = null

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        val sp = Resources.getSystem().displayMetrics.scaledDensity
        textSize = 24f * sp
    }

    fun start(marcherCompute: MarcherCompute) {
        val resolution = marcherCompute.resolution
        val swapchain = SwapchainImpl(
            chainLength = 3,
            factory = { createBitmap(resolution, resolution) }
        )

        srcRect.set(0, 0, resolution, resolution)

        updater = Updater(
            marcherCompute = marcherCompute,
            swapchain = swapchain,
            mean = upsMean,
        ).apply { start() }
    }

    fun stop() {
        val updater = ::updater.take() ?: return
        assert(this.updater == null)
        updater.interrupt()
        capturedBitmap?.let(updater.swapchain::release)
        capturedBitmap = null
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dstRect.set(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        val updater = updater ?: return

        capturedBitmap?.let(updater.swapchain::release)
        capturedBitmap = updater.swapchain.acquire()
        canvas.drawBitmap(capturedBitmap!!, srcRect, dstRect, paint)

        canvas.drawText(
            String.format("UPS: %.1f, P90: %.1f", (1f/upsMean.average), (1f/upsMean.percentile(0.9f))),
            25f,
            height - 25f,
            textPaint
        )

        // Loop render while updater exists
        invalidate()
    }
}