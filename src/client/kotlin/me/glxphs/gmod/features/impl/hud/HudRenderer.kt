package me.glxphs.gmod.features.impl.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.glxphs.gmod.config.GeneralConfig
import me.glxphs.gmod.features.FeatureManager
import me.glxphs.gmod.screens.config.HudPositionScreen
import me.glxphs.gmod.utils.McUtils
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

object HudRenderer {
    fun registerEvents() {
        HudRenderCallback.EVENT.register { matrixStack, _ ->
            val mc = McUtils.mc
            val screen = mc.currentScreen
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

    private fun renderHudFeature(matrixStack: MatrixStack, hud: OverlayFeature, preview: Boolean) {
        val mc = McUtils.mc
        
        val scaleFactor = mc.window.scaleFactor.toFloat()
        val width = mc.window.width / scaleFactor
        
        val scaledX = hud.x.get() / scaleFactor
        val scaledY = hud.y.get() / scaleFactor
        val boxWidth = hud.getScaledBoxWidth()
        
        val textList = if (preview) hud.getPreviewTextList() else hud.getTextList()

        textList.forEachIndexed { index, text ->
            val textWidth = mc.textRenderer.getWidth(text) * hud.scale.get()
            val textX = getAlignedX(width, scaledX, boxWidth, textWidth)

            matrixStack.push()
            matrixStack.translate(
                textX,
                scaledY + index * 10f * hud.scale.get(),
                0.0f
            )
            matrixStack.scale(hud.scale.get(), hud.scale.get(), 1.0f)
            if (GeneralConfig.outlinedText.value) {
                drawWithOutline(
                    matrixStack,
                    text,
                    0f,
                    0f,
                    hud.scale.get()
                )
            } else {
                mc.textRenderer.drawWithShadow(
                    matrixStack,
                    text,
                    0f,
                    0f,
                    0xFFFFFF
                )
            }
            matrixStack.pop()
        }
    }

    private fun drawWithOutline(matrixStack: MatrixStack, text: Text, x: Float, y: Float, scale: Float = 1.0f) {
        RenderSystem.setShaderColor(0f, 0f, 0f, 1f)
        for (xOffset in -1..1) {
            for (yOffset in -1..1) {
                if (xOffset != 0 || yOffset != 0) {
                    McUtils.mc.textRenderer.draw(matrixStack, text, x + xOffset.toFloat() / scale, y + yOffset.toFloat() / scale, 0)
                }
            }
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        McUtils.mc.textRenderer.draw(matrixStack, text, x, y, 0xFFFFFF)
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