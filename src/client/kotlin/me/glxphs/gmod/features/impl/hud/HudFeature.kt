package me.glxphs.gmod.features.impl.hud

import me.glxphs.gmod.config.Config
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.utils.McUtils
import net.minecraft.text.Text

abstract class HudFeature(name: String) : Feature(name) {
    abstract var enabled: Config<Boolean>
    abstract var hudSize: Config<Float>
    abstract var x: Config<Float>
    abstract var y: Config<Float>
//    abstract var unscaledBoxWidth: Float

//    abstract fun renderPreview(matrixStack: MatrixStack, delta: Float, windowWidth: Int, windowHeight: Int, scaledX: Float, scaledY: Float)
//    abstract fun render(matrixStack: MatrixStack, delta: Float, windowWidth: Int, windowHeight: Int, scaledX: Float, scaledY: Float)

    abstract fun getTextList(): List<Text>
    abstract fun getPreviewTextList(): List<Text>

    private fun getUnscaledBoxWidth(): Float {
        return getPreviewTextList().maxOf {
            McUtils.mc.textRenderer.getWidth(it)
        } + 20f
    }

    fun getScaledBoxWidth(): Float {
        return getUnscaledBoxWidth() * hudSize.get()
    }

    private fun getUnscaledBoxHeight(): Float {
        return getPreviewTextList().size * 10f
    }

    fun getScaledBoxHeight(): Float {
        return getUnscaledBoxHeight() * hudSize.get()
    }
}