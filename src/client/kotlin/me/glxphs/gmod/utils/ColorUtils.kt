package me.glxphs.gmod.utils

import com.wynntils.utils.wynn.ColorScaleUtils
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.math.BigDecimal
import java.math.RoundingMode

object ColorUtils {
    fun getPercentageTextComponent(percentage: Float, colorLerp: Boolean, decimalPlaces: Int): MutableText {
        val text = ColorScaleUtils.getPercentageTextComponent(percentage, colorLerp, decimalPlaces)
        val style = text.style
        val percentString = text.string.substring(1, text.string.length - 2)
        return Text.literal(" [$percentString%]").fillStyle(style)
    }
}