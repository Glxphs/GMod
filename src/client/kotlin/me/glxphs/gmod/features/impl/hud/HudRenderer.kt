package me.glxphs.gmod.features.impl.hud

import me.glxphs.gmod.features.FeatureManager
import me.glxphs.gmod.screens.HudPositionScreen
import me.glxphs.gmod.utils.McUtils
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.util.math.MatrixStack

object HudRenderer {
    fun registerEvents() {
        HudRenderCallback.EVENT.register { matrixStack, _ ->
            val mc = McUtils.mc
            val screen = mc.currentScreen ?: return@register
            renderHudFeatures(matrixStack, screen is HudPositionScreen)
        }
    }

    private fun renderHudFeatures(matrixStack: MatrixStack, preview: Boolean) {
        val hudFeatures = FeatureManager.getHudFeatures()
        hudFeatures.forEach {
            if (it.enabled.value) {
                renderHudFeature(matrixStack, it, preview)
            }
        }
    }

    private fun renderHudFeature(matrixStack: MatrixStack, hud: HudFeature, preview: Boolean) {
        val mc = McUtils.mc
        
        val scaleFactor = mc.window.scaleFactor.toFloat()
        val width = mc.window.width / scaleFactor
        val height = mc.window.height / scaleFactor
        
        val scaledX = hud.x.get() / scaleFactor
        val scaledY = hud.y.get() / scaleFactor
        val boxWidth = hud.getScaledBoxWidth()
        
        val textList = if (preview) hud.getPreviewTextList() else hud.getTextList()

        textList.forEachIndexed { index, text ->
            val textWidth = mc.textRenderer.getWidth(text) * hud.hudSize.get()
            val textX = getAlignedX(width, scaledX, boxWidth, textWidth)

            matrixStack.push()
            matrixStack.translate(
                textX,
                scaledY + index * 10f * hud.hudSize.get(),
                0.0f
            )
            matrixStack.scale(hud.hudSize.get(), hud.hudSize.get(), 1.0f)
            mc.textRenderer.drawWithShadow(
                matrixStack,
                text,
                0f,
                0f,
                0xFFFFFF
            )
            matrixStack.pop()
        }
    }

    private fun getAlignedX(windowWidth: Float, scaledX: Float, actualWidth: Float, textWidth: Float): Float {
        return if (scaledX + actualWidth / 2f > windowWidth / 3f * 2f) {
            scaledX + actualWidth - textWidth
        } else if (scaledX + actualWidth / 2f > windowWidth / 3f) {
            scaledX + actualWidth / 2f - textWidth / 2f
        } else {
            scaledX
        }
    }
}