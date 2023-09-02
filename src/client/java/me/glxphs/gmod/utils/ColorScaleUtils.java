//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public final class ColorScaleUtils {
    private static final NavigableMap<Float, TextColor> LERP_MAP;
    private static final NavigableMap<Float, TextColor> FLAT_MAP;

    public ColorScaleUtils() {
    }

    public static MutableText getPercentageTextComponent(float percentage, boolean colorLerp, int decimalPlaces) {
        Style color = Style.EMPTY.withColor(colorLerp ? getPercentageColor(percentage) : getFlatPercentageColor(percentage)).withItalic(false);
        String percentString = (new BigDecimal((double)percentage)).setScale(decimalPlaces, RoundingMode.DOWN).toPlainString();
        return Text.literal(" [" + percentString + "%]").fillStyle(color);
    }

    public static MutableText getPercentageTextComponentNoBrackets(float percentage, boolean colorLerp, int decimalPlaces) {
        Style color = Style.EMPTY.withColor(colorLerp ? getPercentageColor(percentage) : getFlatPercentageColor(percentage)).withItalic(false);
        String percentString = (new BigDecimal((double)percentage)).setScale(decimalPlaces, RoundingMode.DOWN).toPlainString();
        return Text.literal(percentString).fillStyle(color);
    }

    private static TextColor getPercentageColor(float percentage) {
        Map.Entry<Float, TextColor> lowerEntry = LERP_MAP.floorEntry(percentage);
        Map.Entry<Float, TextColor> higherEntry = LERP_MAP.ceilingEntry(percentage);
        if (lowerEntry == null) {
            return (TextColor)higherEntry.getValue();
        } else if (higherEntry == null) {
            return (TextColor)lowerEntry.getValue();
        } else if (Objects.equals(lowerEntry.getKey(), higherEntry.getKey())) {
            return (TextColor)lowerEntry.getValue();
        } else {
            float t = MathUtils.inverseLerp((Float)lowerEntry.getKey(), (Float)higherEntry.getKey(), percentage);
            int lowerColor = ((TextColor)lowerEntry.getValue()).getRgb();
            int higherColor = ((TextColor)higherEntry.getValue()).getRgb();
            int r = (int)MathUtils.lerp((float)(lowerColor >> 16 & 255), (float)(higherColor >> 16 & 255), t);
            int g = (int)MathUtils.lerp((float)(lowerColor >> 8 & 255), (float)(higherColor >> 8 & 255), t);
            int b = (int)MathUtils.lerp((float)(lowerColor & 255), (float)(higherColor & 255), t);
            return TextColor.fromRgb(r << 16 | g << 8 | b);
        }
    }

    private static TextColor getFlatPercentageColor(float percentage) {
        return (TextColor)FLAT_MAP.higherEntry(percentage).getValue();
    }

    static {
        LERP_MAP = new TreeMap(Map.of(0.0F, TextColor.fromFormatting(Formatting.RED), 70.0F, TextColor.fromFormatting(Formatting.YELLOW), 90.0F, TextColor.fromFormatting(Formatting.GREEN), 100.0F, TextColor.fromFormatting(Formatting.AQUA)));
        FLAT_MAP = new TreeMap(Map.of(30.0F, TextColor.fromFormatting(Formatting.RED), 80.0F, TextColor.fromFormatting(Formatting.YELLOW), 96.0F, TextColor.fromFormatting(Formatting.GREEN), Float.MAX_VALUE, TextColor.fromFormatting(Formatting.AQUA)));
    }
}
