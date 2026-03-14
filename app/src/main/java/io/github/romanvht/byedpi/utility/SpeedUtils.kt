package io.github.romanvht.byedpi.utility

import android.net.TrafficStats
import kotlin.math.max

object SpeedUtils {
    data class Sample(val rx: Long, val tx: Long, val timeMs: Long)

    fun nowSample(): Sample =
        Sample(TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes(), System.currentTimeMillis())

    fun calcSpeed(prev: Sample, curr: Sample): Pair<Long, Long> {
        val dt = max(1L, curr.timeMs - prev.timeMs)
        val rx = max(0L, curr.rx - prev.rx)
        val tx = max(0L, curr.tx - prev.tx)
        val rxPerSec = rx * 1000 / dt
        val txPerSec = tx * 1000 / dt
        return Pair(rxPerSec, txPerSec)
    }

    fun formatSpeed(bps: Long): String {
        val kb = 1024.0
        val mb = kb * 1024.0
        val gb = mb * 1024.0
        val v = bps.toDouble()
        return when {
            v >= gb -> String.format("%.2f GB/s", v / gb)
            v >= mb -> String.format("%.2f MB/s", v / mb)
            v >= kb -> String.format("%.1f KB/s", v / kb)
            else -> "$bps B/s"
        }
    }
}
