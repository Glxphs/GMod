package me.glxphs.gmod.features.impl.hud

import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.utils.McUtils
import net.minecraft.text.Text

abstract class OverlayFeature(name: String) : Feature(name) {
    abstract var scale: ConfigValue<Float>
    abstract var x: ConfigValue<Float>
    abstract var y: ConfigValue<Float>

    abstract fun getTextList(): List<Text>
    abstract fun getPreviewTextList(): List<Text>

    private fun getUnscaledBoxWidth(): Float {
        return getPreviewTextList().maxOf {
            McUtils.mc.textRenderer.getWidth(it)
        } + 20f
    }

    fun getScaledBoxWidth(): Float {
        return getUnscaledBoxWidth() * scale.get()
    }

    private fun getUnscaledBoxHeight(): Float {
        return getPreviewTextList().size * 10f - 1f
    }

    fun getScaledBoxHeight(): Float {
        return getUnscaledBoxHeight() * scale.get()
    }
}