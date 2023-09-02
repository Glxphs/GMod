//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import net.minecraft.util.Formatting;

public class CustomColor {
    public static final CustomColor NONE = new CustomColor(-1, -1, -1, -1);
    private static final Pattern HEX_PATTERN = Pattern.compile("#?([0-9a-fA-F]{6})");
    private static final Pattern STRING_PATTERN = Pattern.compile("rgba\\((\\d+),(\\d+),(\\d+),(\\d+)\\)");
    private static final Map<String, CustomColor> REGISTERED_HASHED_COLORS = new HashMap();
    public final int r;
    public final int g;
    public final int b;
    public final int a;

    public CustomColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public CustomColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public CustomColor(float r, float g, float b) {
        this(r, g, b, 1.0F);
    }

    public CustomColor(float r, float g, float b, float a) {
        this.r = (int)(r * 255.0F);
        this.g = (int)(g * 255.0F);
        this.b = (int)(b * 255.0F);
        this.a = (int)(a * 255.0F);
    }

    public CustomColor(CustomColor color) {
        this(color.r, color.g, color.b, color.a);
    }

    public CustomColor(CustomColor color, int alpha) {
        this(color.r, color.g, color.b, alpha);
    }

    public CustomColor(String toParse) {
        String noSpace = toParse.replace(" ", "");
        CustomColor parseTry = fromString(noSpace);
        if (parseTry == NONE) {
            parseTry = fromHexString(noSpace);
            if (parseTry == NONE) {
                throw new RuntimeException("Failed to parse CustomColor");
            }
        }

        this.r = parseTry.r;
        this.g = parseTry.g;
        this.b = parseTry.b;
        this.a = parseTry.a;
    }

    public static CustomColor fromChatFormatting(Formatting cf) {
        return fromInt(cf.getColorValue() | -16777216);
    }

    public static CustomColor fromInt(int num) {
        return new CustomColor(num >> 16 & 255, num >> 8 & 255, num & 255, num >> 24 & 255);
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static CustomColor fromHSV(float h, float s, float v, float a) {
        a = clamp(a, 0.0F, 1.0F);
        if (v <= 0.0F) {
            return new CustomColor(0.0F, 0.0F, 0.0F, a);
        } else {
            if (v > 1.0F) {
                v = 1.0F;
            }

            if (s <= 0.0F) {
                return new CustomColor(v, v, v, a);
            } else {
                if (s > 1.0F) {
                    s = 1.0F;
                }

                float vh = (h % 1.0F + 1.0F) * 6.0F % 6.0F;
                int vi = (int) Math.floor(vh);
                float v1 = v * (1.0F - s);
                float v2 = v * (1.0F - s * (vh - (float)vi));
                float v3 = v * (1.0F - s * (1.0F - (vh - (float)vi)));
                CustomColor var10000;
                switch (vi) {
                    case 0:
                        var10000 = new CustomColor(v, v3, v1, a);
                        break;
                    case 1:
                        var10000 = new CustomColor(v2, v, v1, a);
                        break;
                    case 2:
                        var10000 = new CustomColor(v1, v, v3, a);
                        break;
                    case 3:
                        var10000 = new CustomColor(v1, v2, v, a);
                        break;
                    case 4:
                        var10000 = new CustomColor(v3, v1, v, a);
                        break;
                    default:
                        var10000 = new CustomColor(v, v1, v2, a);
                }

                return var10000;
            }
        }
    }

    public static CustomColor fromHexString(String hex) {
        Matcher hexMatcher = HEX_PATTERN.matcher(hex.trim());
        return !hexMatcher.matches() ? NONE : fromInt(Integer.parseInt(hexMatcher.group(1), 16)).withAlpha(255);
    }

    public static CustomColor fromString(String string) {
        Matcher stringMatcher = STRING_PATTERN.matcher(string.trim());
        return !stringMatcher.matches() ? NONE : new CustomColor(Integer.parseInt(stringMatcher.group(1)), Integer.parseInt(stringMatcher.group(2)), Integer.parseInt(stringMatcher.group(3)), Integer.parseInt(stringMatcher.group(4)));
    }

    public static CustomColor colorForStringHash(String input) {
        if (REGISTERED_HASHED_COLORS.containsKey(input)) {
            return (CustomColor)REGISTERED_HASHED_COLORS.get(input);
        } else {
            CRC32 crc32 = new CRC32();
            crc32.update(input.getBytes(StandardCharsets.UTF_8));
            CustomColor color = fromInt((int)crc32.getValue() & 16777215).withAlpha(255);
            REGISTERED_HASHED_COLORS.put(input, color);
            return color;
        }
    }

    public CustomColor withAlpha(int a) {
        return new CustomColor(this, a);
    }

    public CustomColor withAlpha(float a) {
        return new CustomColor(this, (int)(a * 255.0F));
    }

    public int asInt() {
        int a = Math.min(this.a, 255);
        int r = Math.min(this.r, 255);
        int g = Math.min(this.g, 255);
        int b = Math.min(this.b, 255);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public float[] asFloatArray() {
        return new float[]{(float)this.r / 255.0F, (float)this.g / 255.0F, (float)this.b / 255.0F};
    }

    public String toHexString() {
        String hex = Integer.toHexString(this.asInt());
        hex = hex.length() > 7 ? hex.substring(2) : hex.substring(1);
        hex = "#" + hex;
        return hex;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CustomColor color)) {
            return false;
        } else {
            return this.r == color.r && this.g == color.g && this.b == color.b && this.a == color.a;
        }
    }

    public String toString() {
        return this.toHexString();
    }

    public static class CustomColorSerializer implements JsonSerializer<CustomColor>, JsonDeserializer<CustomColor> {
        public CustomColorSerializer() {
        }

        public CustomColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CustomColor customColor = CustomColor.fromHexString(json.getAsString());
            return customColor == CustomColor.NONE ? CustomColor.fromString(json.getAsString()) : customColor;
        }

        public JsonElement serialize(CustomColor src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.toString());
        }
    }
}
