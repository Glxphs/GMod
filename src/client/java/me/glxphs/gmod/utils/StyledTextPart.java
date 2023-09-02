//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class StyledTextPart {
    private final String text;
    private final PartStyle style;
    private final StyledText parent;

    public StyledTextPart(String text, Style style, StyledText parent, Style parentStyle) {
        this.parent = parent;
        this.text = text;
        this.style = PartStyle.fromStyle(style, this, parentStyle);
    }

    StyledTextPart(StyledTextPart part, StyledText parent) {
        this.text = part.text;
        this.style = new PartStyle(part.style, this);
        this.parent = parent;
    }

    private StyledTextPart(StyledTextPart part, PartStyle style, StyledText parent) {
        this.text = part.text;
        this.style = style;
        this.parent = parent;
    }

    static List<StyledTextPart> fromCodedString(String codedString, Style style, StyledText parent, Style parentStyle) {
        List<StyledTextPart> parts = new ArrayList();
        Style currentStyle = style;
        StringBuilder currentString = new StringBuilder();
        boolean nextIsFormatting = false;
        char[] var8 = codedString.toCharArray();
        int var9 = var8.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            char current = var8[var10];
            if (nextIsFormatting) {
                nextIsFormatting = false;
                Formatting formatting = Formatting.byCode(current);
                if (formatting == null) {
                    currentString.append('§');
                    currentString.append(current);
                } else {
                    if (!currentString.isEmpty()) {
                        currentStyle = currentStyle.withClickEvent(style.getClickEvent()).withHoverEvent(style.getHoverEvent());
                        parts.add(new StyledTextPart(currentString.toString(), currentStyle, (StyledText)null, parentStyle));
                        currentString = new StringBuilder();
                    }

                    if (formatting.isColor()) {
                        currentStyle = Style.EMPTY.withColor(formatting);
                    } else {
                        currentStyle = currentStyle.withFormatting(formatting);
                    }
                }
            } else if (current == 167) {
                nextIsFormatting = true;
            } else {
                currentString.append(current);
            }
        }

        if (!currentString.isEmpty()) {
            currentStyle = currentStyle.withClickEvent(style.getClickEvent()).withHoverEvent(style.getHoverEvent());
            parts.add(new StyledTextPart(currentString.toString(), currentStyle, (StyledText)null, parentStyle));
        }

        return parts;
    }

    public String getString(PartStyle previousStyle, PartStyle.StyleType type) {
        String var10000 = this.style.asString(previousStyle, type);
        return var10000 + this.text;
    }

    public StyledText getParent() {
        return this.parent;
    }

    public PartStyle getPartStyle() {
        return this.style;
    }

    public StyledTextPart withStyle(PartStyle style) {
        return new StyledTextPart(this, style, this.parent);
    }

    public StyledTextPart withStyle(Function<PartStyle, PartStyle> function) {
        return this.withStyle((PartStyle)function.apply(this.style));
    }

    public MutableText getComponent() {
        return Text.literal(this.text).fillStyle(this.style.getStyle());
    }

    StyledTextPart stripLeading() {
        return new StyledTextPart(this.text.stripLeading(), this.style.getStyle(), this.parent, (Style)null);
    }

    StyledTextPart stripTrailing() {
        return new StyledTextPart(this.text.stripTrailing(), this.style.getStyle(), this.parent, (Style)null);
    }

    boolean isEmpty() {
        return this.text.isEmpty();
    }

    boolean isBlank() {
        return this.text.isBlank();
    }

    public int length() {
        return this.text.length();
    }

    public String toString() {
        return "StyledTextPart[text=" + this.text + ", style=" + this.style + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            StyledTextPart that = (StyledTextPart)o;
            return Objects.equals(this.text, that.text) && Objects.equals(this.style, that.style);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.text, this.style});
    }
}
