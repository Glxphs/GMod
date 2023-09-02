package me.glxphs.gmod.utils

object MathUtils {
    @JvmStatic
    fun lerp(start: Float, end: Float, delta: Float): Float {
        return start + (end - start) * delta
    }

    @JvmStatic
    fun inverseLerp(start: Float, end: Float, value: Float): Float {
        return (value - start) / (end - start)
    }
}