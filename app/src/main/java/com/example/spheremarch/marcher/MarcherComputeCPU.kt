package com.example.spheremarch.marcher

import android.graphics.Bitmap
import androidx.core.graphics.set
import com.example.spheremarch.math.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue

class MarcherComputeCPU(
    override val resolution: Int,
) : MarcherCompute {

    private var config: MarcherConfig? = null

    override fun applyConfig(marcherConfig: MarcherConfig) {
        this.config = marcherConfig
    }

    private val parallelism = 1

    override fun update(bitmap: Bitmap) {
        val config = config ?: return

        runBlocking(Dispatchers.Default) {
            for (i in 0 until parallelism) launch {
                for (x in i until resolution step parallelism) for (y in 0 until resolution) {
                    bitmap[x, y] = config.runRay(x, y)
                }
            }
        }
    }

    private fun MarcherViewer.marchDirection(x: Int, y: Int): Vec3 {
        val xTilt = ((x / resolution.toFloat()) - 0.5f) * 2f
        val yTilt = ((y / resolution.toFloat()) - 0.5f) * 2f
        val ogDir = (eyeLookAt - eyePosition).normalizeInPlace()
        val xDir = ogDir.cross(eyeUp).normalizeInPlace()
        val yDir = ogDir.cross(xDir).normalizeInPlace()

//        return (ogDir + xDir * xTilt + yDir * yTilt).normalized

        xDir *= xTilt
        yDir *= yTilt
        ogDir += xDir
        ogDir += yDir

        return ogDir.normalized
    }

    private fun MarcherConfig.runRay(x: Int, y: Int): Int {
        val origin = viewer.eyePosition
        val dir = viewer.marchDirection(x, y)

        var travelDist = 0f
        var hopCount = 0

        with(ray) {
            do {
                hopCount++

                val p = origin + dir * travelDist
                val (dist, color) = scene.mapDistance(p, maxTravelDist - travelDist)

                travelDist += dist
                if (dist.absoluteValue < 0.0001) {
                    val sun = vec3(-1f, 1f, 1f).normalized
                    val normal = scene.findNormal(p)
                    val ac = calcAO(p, normal)

                    val l = sun.dot(normal).coerceIn(0.2f, 1f)

                    val lin = ac * l
                    return (color * lin).toColor()
                }
            } while (hopCount < maxHopCount && travelDist < maxTravelDist)
        }

        val hopArg = 0f // (hopCount / ray.maxHopCount.toFloat()).pow(2)
        return (vec4One * hopArg).toColor(a = 1f)
    }

    // Ambient Occlusion
    // https://iquilezles.org/articles/nvscene2008/rwwtt.pdf
    private fun MarcherConfig.calcAO(pos: Vec3, normal: Vec3): Float {
        var occ = 0f
        var sca = 1f
        for (i in 0 until 5) {
            val h = .01f + 0.12f * i.toFloat() / 4f
            val d = scene.mapDistance(pos + normal * h, ray.maxTravelDist).first
            occ += (h - d) * sca
            sca *= 0.95f
            if (occ > 0.35f) break
        }
        return (1f - 3f * occ).coerceIn(0f, 1f) * (.5f + .5f * normal.y)
    }
}