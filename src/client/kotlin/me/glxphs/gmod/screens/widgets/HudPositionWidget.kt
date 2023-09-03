package me.glxphs.gmod.screens.widgets

import me.glxphs.gmod.features.impl.hud.OverlayFeature
import me.glxphs.gmod.utils.McUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.apache.commons.lang3.tuple.MutablePair
import kotlin.math.abs

class HudPositionWidget(val hud: OverlayFeature) : ClickableWidget(
    0,
    0,
    hud.getScaledBoxWidth().toInt(),
    hud.getScaledBoxHeight().toInt(),
    Text.of(hud.name)
) {
    private val textRenderer = MinecraftClient.getInstance().textRenderer

    private var moving = false

    val SNAP_DISTANCE = 5f
    private val deltaSumSinceSnap = MutablePair(0f, 0f)

    private fun getSnappingLines(): List<Line> {
        val list = mutableListOf<Line>()

        val window = McUtils.mc.window
        val scaleFactor = window.scaleFactor.toFloat()

        val windowWidth = window.width / scaleFactor
        val windowHeight = window.height / scaleFactor

        val centerXLine = Line(Axis.X, windowWidth / 2f)
        val centerYLine = Line(Axis.Y, windowHeight / 2f)
        list.add(centerXLine)
        list.add(centerYLine)

        return list
    }

    override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (matrices == null) return

        val scaleFactor = MinecraftClient.getInstance().window.scaleFactor.toFloat()

        val window = McUtils.mc.window
        val windowWidth = window.width / scaleFactor
        val windowHeight = window.height / scaleFactor

        x = (hud.x.value / scaleFactor).toInt()
        y = (hud.y.value / scaleFactor).toInt()

        val textColor = if (this.isHovered) 0x55FF55 else 0x55FFFF

        fill(matrices, x, y, x + width, y + height, 0x40000000 + textColor)

        // draw corners
        val topRight = Pair(x + width + 2, y - 2)
        fill(
            matrices,
            topRight.first - 5,
            topRight.second,
            topRight.first,
            topRight.second + 2,
            0xFF000000.toInt() + textColor
        )
        fill(
            matrices,
            topRight.first - 2,
            topRight.second,
            topRight.first,
            topRight.second + 5,
            0xFF000000.toInt() + textColor
        )

        val topLeft = Pair(x - 2, y - 2)
        fill(
            matrices,
            topLeft.first,
            topLeft.second,
            topLeft.first + 5,
            topLeft.second + 2,
            0xFF000000.toInt() + textColor
        )
        fill(
            matrices,
            topLeft.first,
            topLeft.second,
            topLeft.first + 2,
            topLeft.second + 5,
            0xFF000000.toInt() + textColor
        )

        val bottomRight = Pair(x + width + 2, y + height + 2)
        fill(
            matrices,
            bottomRight.first - 5,
            bottomRight.second - 2,
            bottomRight.first,
            bottomRight.second,
            0xFF000000.toInt() + textColor
        )
        fill(
            matrices,
            bottomRight.first - 2,
            bottomRight.second - 5,
            bottomRight.first,
            bottomRight.second,
            0xFF000000.toInt() + textColor
        )

        val bottomLeft = Pair(x - 2, y + height + 2)
        fill(
            matrices,
            bottomLeft.first,
            bottomLeft.second - 2,
            bottomLeft.first + 5,
            bottomLeft.second,
            0xFF000000.toInt() + textColor
        )
        fill(
            matrices,
            bottomLeft.first,
            bottomLeft.second - 5,
            bottomLeft.first + 2,
            bottomLeft.second,
            0xFF000000.toInt() + textColor
        )

        drawCenteredTextWithShadow(
            matrices,
            textRenderer,
            message,
            x + width / 2,
            y + height + 5,
            0xFF000000.toInt() + textColor
        )

        if (moving) {
            getSnappingLines().forEach {
                when (it.axis) {
                    Axis.X -> {
                        if (!it.isWithinSnapDistance(x.toFloat())) return@forEach
                        fill(
                            matrices,
                            it.coord.toInt(),
                            0,
                            it.coord.toInt() + 1,
                            windowHeight.toInt(),
                            0xFF000000.toInt() + textColor
                        )
                    }

                    Axis.Y -> {
                        if (!it.isWithinSnapDistance(y.toFloat())) return@forEach
                        fill(
                            matrices,
                            0,
                            it.coord.toInt(),
                            windowWidth.toInt(),
                            it.coord.toInt() + 1,
                            0xFF000000.toInt() + textColor
                        )
                    }
                }
            }
        }
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        moving = true
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        moving = false
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        if (!moving) return

        val window = McUtils.mc.window
        val scaleFactor = window.scaleFactor.toFloat()

        val scaledX = hud.x.value / scaleFactor
        val scaledY = hud.y.value / scaleFactor

        var newDeltaX = deltaX
        var newDeltaY = deltaY

        getSnappingLines().forEach {
            when (it.axis) {
                Axis.X -> {
                    if (it.isWithinSnapDistance((scaledX + deltaSumSinceSnap.left + deltaX).toFloat())) {
                        newDeltaX = (it.coord - it.getWidthOrHeight()/2 - scaledX).toDouble()
                        deltaSumSinceSnap.left += deltaX.toFloat()
                    } else {
                        newDeltaX += deltaSumSinceSnap.left
                        deltaSumSinceSnap.left = 0f
                    }
                }

                Axis.Y -> {
                    if (it.isWithinSnapDistance((scaledY + deltaSumSinceSnap.right + deltaY).toFloat())) {
                        newDeltaY = (it.coord - it.getWidthOrHeight()/2 - scaledY).toDouble()
                        deltaSumSinceSnap.right += deltaY.toFloat()
                    } else {
                        newDeltaY += deltaSumSinceSnap.right
                        deltaSumSinceSnap.right = 0f
                    }
                }
            }
        }

        hud.x.value += newDeltaX.toFloat() * scaleFactor
        hud.y.value += newDeltaY.toFloat() * scaleFactor
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

    enum class Axis {
        X, Y
    }

    inner class Line(val axis: Axis, val coord: Float) {
        fun getDistance(coord: Float): Float {
            return abs(coord - this.coord)
        }

        fun isWithinSnapDistance(coord: Float): Boolean {
            return getDistance(coord + getWidthOrHeight() / 2) < SNAP_DISTANCE // center
//                        || getDistance(coord) < SNAP_DISTANCE // left/top
//                        || getDistance(coord + getWidthOrHeight()) < SNAP_DISTANCE // right/bottom
        }

        fun getWidthOrHeight(): Float {
            return when (axis) {
                Axis.X -> width.toFloat()
                Axis.Y -> height.toFloat()
            }
        }

        fun getCenter(): Float {
            return coord + getWidthOrHeight() / 2
        }
    }
}