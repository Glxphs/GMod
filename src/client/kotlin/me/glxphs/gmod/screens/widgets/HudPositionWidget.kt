package me.glxphs.gmod.screens.widgets

import me.glxphs.gmod.features.impl.hud.HudFeature
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class HudPositionWidget(val hud: HudFeature) : ClickableWidget(
    0,
    0,
    hud.getScaledBoxWidth().toInt(),
    hud.getScaledBoxHeight().toInt(),
    Text.of(hud.name)
) {
    val textRenderer = MinecraftClient.getInstance().textRenderer

    var moving = false

    override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (matrices == null) return

        val scaleFactor = MinecraftClient.getInstance().window.scaleFactor.toFloat()

        x = (hud.x.get() / scaleFactor).toInt()
        y = (hud.y.get() / scaleFactor).toInt()

        val textColor = if (this.isHovered) 0x55FF55 else 0x55FFFF

        fill(matrices, x, y, x + width, y + height, 0x40000000 + textColor)

        // draw corners
        val topRight = Pair(x + width + 2, y - 2)
        fill(matrices, topRight.first - 5, topRight.second, topRight.first, topRight.second + 2, 0xFF000000.toInt() + textColor)
        fill(matrices, topRight.first - 2, topRight.second, topRight.first, topRight.second + 5, 0xFF000000.toInt() + textColor)

        val topLeft = Pair(x - 2, y - 2)
        fill(matrices, topLeft.first, topLeft.second, topLeft.first + 5, topLeft.second + 2, 0xFF000000.toInt() + textColor)
        fill(matrices, topLeft.first, topLeft.second, topLeft.first + 2, topLeft.second + 5, 0xFF000000.toInt() + textColor)

        val bottomRight = Pair(x + width + 2, y + height + 2)
        fill(matrices, bottomRight.first - 5, bottomRight.second - 2, bottomRight.first, bottomRight.second, 0xFF000000.toInt() + textColor)
        fill(matrices, bottomRight.first - 2, bottomRight.second - 5, bottomRight.first, bottomRight.second, 0xFF000000.toInt() + textColor)

        val bottomLeft = Pair(x - 2, y + height + 2)
        fill(matrices, bottomLeft.first, bottomLeft.second - 2, bottomLeft.first + 5, bottomLeft.second, 0xFF000000.toInt() + textColor)
        fill(matrices, bottomLeft.first, bottomLeft.second - 5, bottomLeft.first + 2, bottomLeft.second, 0xFF000000.toInt() + textColor)

        drawCenteredTextWithShadow(matrices, textRenderer, message, x + width / 2, y + height + 5, 0xFF000000.toInt() + textColor)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        moving = true
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        moving = false
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        if (!moving) return

        val scaleFactor = MinecraftClient.getInstance().window.scaleFactor.toFloat()

        hud.x.set(hud.x.get() + deltaX.toFloat() * scaleFactor)
        hud.y.set(hud.y.get() + deltaY.toFloat() * scaleFactor)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        if (builder == null) return
        builder.put(NarrationPart.TITLE, this.narrationMessage as Text)
        if (active) {
            if (this.isFocused) {
                builder.put(
                    NarrationPart.USAGE,
                    Text.translatable("narration.button.usage.focused") as Text
                )
            } else {
                builder.put(
                    NarrationPart.USAGE,
                    Text.translatable("narration.button.usage.hovered") as Text
                )
            }
        }
    }
}