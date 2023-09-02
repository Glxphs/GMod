//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public final class PartStyle {
    private static final String STYLE_PREFIX = "§";
    private final StyledTextPart owner;
    private final CustomColor color;
    private final boolean obfuscated;
    private final boolean bold;
    private final boolean strikethrough;
    private final boolean underlined;
    private final boolean italic;
    private final ClickEvent clickEvent;
    private final HoverEvent hoverEvent;

    private PartStyle(StyledTextPart owner, CustomColor color, boolean obfuscated, boolean bold, boolean strikethrough, boolean underlined, boolean italic, ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.owner = owner;
        this.color = color;
        this.obfuscated = obfuscated;
        this.bold = bold;
        this.strikethrough = strikethrough;
        this.underlined = underlined;
        this.italic = italic;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
    }

    PartStyle(PartStyle partStyle, StyledTextPart owner) {
        this.owner = owner;
        this.color = partStyle.color;
        this.obfuscated = partStyle.obfuscated;
        this.bold = partStyle.bold;
        this.strikethrough = partStyle.strikethrough;
        this.underlined = partStyle.underlined;
        this.italic = partStyle.italic;
        this.clickEvent = partStyle.clickEvent;
        this.hoverEvent = partStyle.hoverEvent;
    }

    static PartStyle fromStyle(Style style, StyledTextPart owner, Style parentStyle) {
        Style inheritedStyle;
        if (parentStyle == null) {
            inheritedStyle = style;
        } else {
            inheritedStyle = style.withParent(parentStyle);
            inheritedStyle = inheritedStyle.withClickEvent(style.getClickEvent()).withHoverEvent(style.getHoverEvent()).withInsertion(style.getInsertion()).withFont(style.getFont());
        }

        return new PartStyle(owner, inheritedStyle.getColor() == null ? CustomColor.NONE : CustomColor.fromInt(inheritedStyle.getColor().getRgb()), inheritedStyle.isObfuscated(), inheritedStyle.isBold(), inheritedStyle.isStrikethrough(), inheritedStyle.isUnderlined(), inheritedStyle.isItalic(), inheritedStyle.getClickEvent(), inheritedStyle.getHoverEvent());
    }

    public String asString(PartStyle previousStyle, StyleType type) {
        if (type == StyleType.NONE) {
            return "";
        } else {
            StringBuilder styleString = new StringBuilder();
            boolean skipFormatting = false;
            if (previousStyle != null && (this.color == CustomColor.NONE || previousStyle.color.equals(this.color))) {
                String differenceString = this.tryConstructDifference(previousStyle);
                if (differenceString != null) {
                    styleString.append(differenceString);
                    skipFormatting = true;
                } else {
                    styleString.append("§").append(Formatting.RESET.getCode());
                }
            }

            if (!skipFormatting) {
                if (this.color != CustomColor.NONE) {
                    Optional<Formatting> chatFormatting = Arrays.stream(Formatting.values()).filter(Formatting::isColor).filter((c) -> {
                        return c.getColorValue() == this.color.asInt();
                    }).findFirst();
                    if (chatFormatting.isPresent()) {
                        styleString.append("§").append(((Formatting)chatFormatting.get()).getCode());
                    } else {
                        styleString.append("§").append(this.color.toHexString());
                    }
                }

                if (this.obfuscated) {
                    styleString.append("§").append(Formatting.OBFUSCATED.getCode());
                }

                if (this.bold) {
                    styleString.append("§").append(Formatting.BOLD.getCode());
                }

                if (this.strikethrough) {
                    styleString.append("§").append(Formatting.STRIKETHROUGH.getCode());
                }

                if (this.underlined) {
                    styleString.append("§").append(Formatting.UNDERLINE.getCode());
                }

                if (this.italic) {
                    styleString.append("§").append(Formatting.ITALIC.getCode());
                }
            }

            if (type == StyleType.INCLUDE_EVENTS) {
                if (this.clickEvent != null) {
                    styleString.append("§").append("[").append(this.owner.getParent().addClickEvent(this.clickEvent)).append("]");
                }

                if (this.hoverEvent != null) {
                    styleString.append("§").append("<").append(this.owner.getParent().addHoverEvent(this.hoverEvent)).append(">");
                }
            }

            return styleString.toString();
        }
    }

    public Style getStyle() {
        Style reconstructedStyle = Style.EMPTY.withObfuscated(this.obfuscated).withBold(this.bold).withStrikethrough(this.strikethrough).withUnderline(this.underlined).withItalic(this.italic).withClickEvent(this.clickEvent).withHoverEvent(this.hoverEvent);
        if (this.color != CustomColor.NONE) {
            reconstructedStyle = reconstructedStyle.withColor(this.color.asInt());
        }

        return reconstructedStyle;
    }

    public PartStyle withColor(Formatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("ChatFormatting " + color + " is not a color!");
        } else {
            CustomColor newColor = CustomColor.fromInt(color.getColorValue());
            return new PartStyle(this.owner, newColor, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent);
        }
    }

    public boolean isBold() {
        return this.bold;
    }

    public boolean isObfuscated() {
        return this.obfuscated;
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public boolean isUnderlined() {
        return this.underlined;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public PartStyle withBold(boolean bold) {
        return new PartStyle(this.owner, this.color, this.obfuscated, bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent);
    }

    public PartStyle withObfuscated(boolean obfuscated) {
        return new PartStyle(this.owner, this.color, obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent);
    }

    public PartStyle withStrikethrough(boolean strikethrough) {
        return new PartStyle(this.owner, this.color, this.obfuscated, this.bold, strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent);
    }

    public PartStyle withUnderlined(boolean underlined) {
        return new PartStyle(this.owner, this.color, this.obfuscated, this.bold, this.strikethrough, underlined, this.italic, this.clickEvent, this.hoverEvent);
    }

    public PartStyle withItalic(boolean italic) {
        return new PartStyle(this.owner, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, italic, this.clickEvent, this.hoverEvent);
    }

    public PartStyle withClickEvent(ClickEvent clickEvent) {
        return new PartStyle(this.owner, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, clickEvent, this.hoverEvent);
    }

    public PartStyle withHoverEvent(HoverEvent hoverEvent) {
        return new PartStyle(this.owner, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, hoverEvent);
    }

    private String tryConstructDifference(PartStyle oldStyle) {
        StringBuilder add = new StringBuilder();
        int oldColorInt = oldStyle.color.asInt();
        int newColorInt = this.color.asInt();
        if (oldColorInt == -1) {
            if (newColorInt != -1) {
                Optional var10000 = Arrays.stream(Formatting.values()).filter((c) -> {
                    return c.isColor() && newColorInt == c.getColorValue();
                }).findFirst();
                Objects.requireNonNull(add);
                var10000.ifPresent(add::append);
            }
        } else if (oldColorInt != newColorInt) {
            return null;
        }

        if (oldStyle.obfuscated && !this.obfuscated) {
            return null;
        } else {
            if (!oldStyle.obfuscated && this.obfuscated) {
                add.append(Formatting.OBFUSCATED);
            }

            if (oldStyle.bold && !this.bold) {
                return null;
            } else {
                if (!oldStyle.bold && this.bold) {
                    add.append(Formatting.BOLD);
                }

                if (oldStyle.strikethrough && !this.strikethrough) {
                    return null;
                } else {
                    if (!oldStyle.strikethrough && this.strikethrough) {
                        add.append(Formatting.STRIKETHROUGH);
                    }

                    if (oldStyle.underlined && !this.underlined) {
                        return null;
                    } else {
                        if (!oldStyle.underlined && this.underlined) {
                            add.append(Formatting.UNDERLINE);
                        }

                        if (oldStyle.italic && !this.italic) {
                            return null;
                        } else {
                            if (!oldStyle.italic && this.italic) {
                                add.append(Formatting.ITALIC);
                            }

                            return add.toString();
                        }
                    }
                }
            }
        }
    }

    public String toString() {
        return "PartStyle{color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.clickEvent + ", hoverEvent=" + this.hoverEvent + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PartStyle partStyle = (PartStyle)o;
            return this.bold == partStyle.bold && this.italic == partStyle.italic && this.underlined == partStyle.underlined && this.strikethrough == partStyle.strikethrough && this.obfuscated == partStyle.obfuscated && Objects.equals(this.color, partStyle.color) && Objects.equals(this.clickEvent, partStyle.clickEvent) && Objects.equals(this.hoverEvent, partStyle.hoverEvent);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent});
    }

    public static enum StyleType {
        INCLUDE_EVENTS,
        DEFAULT,
        NONE;

        private StyleType() {
        }
    }
}
