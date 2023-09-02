//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;
import net.minecraft.util.Pair;

import me.glxphs.gmod.utils.PartStyle.StyleType;

public final class StyledText implements Iterable<StyledTextPart> {
    public static final StyledText EMPTY = new StyledText(List.of(), List.of(), List.of());
    private final List<StyledTextPart> parts;
    private final List<ClickEvent> clickEvents;
    private final List<HoverEvent> hoverEvents;

    private StyledText(List<StyledTextPart> parts, List<ClickEvent> clickEvents, List<HoverEvent> hoverEvents) {
        this.parts = parts.stream().filter((styledTextPart) -> {
            return !styledTextPart.isEmpty();
        }).map((styledTextPart) -> {
            return new StyledTextPart(styledTextPart, this);
        }).collect(Collectors.toList());
        this.clickEvents = new ArrayList<>(clickEvents);
        this.hoverEvents = new ArrayList<>(hoverEvents);
    }

    public static StyledText fromComponent(Text component) {
        List<StyledTextPart> parts = new ArrayList();
        List<ClickEvent> clickEvents = new ArrayList();
        List<HoverEvent> hoverEvents = new ArrayList();
        Deque<Pair<Text, Style>> deque = new LinkedList();
        deque.add(new Pair(component, Style.EMPTY));

        while(!deque.isEmpty()) {
            Pair<Text, Style> currentPair = (Pair)deque.pop();
            Text current = (Text)currentPair.getLeft();
            Style parentStyle = (Style)currentPair.getRight();
            String componentString = MutableText.of(current.getContent()).getString();
            List<StyledTextPart> styledTextParts = StyledTextPart.fromCodedString(componentString, current.getStyle(), (StyledText)null, parentStyle);
            Style styleToFollowForChildren = current.getStyle().withParent(parentStyle);
            List<Pair<Text, Style>> siblingPairs = (List)current.getSiblings().stream().map((sibling) -> {
                return new Pair(sibling, styleToFollowForChildren);
            }).collect(Collectors.toList());
            Collections.reverse(siblingPairs);
            Objects.requireNonNull(deque);
            siblingPairs.forEach(deque::addFirst);
            parts.addAll(styledTextParts.stream().filter((part) -> {
                return !part.isEmpty();
            }).toList());
        }

        return new StyledText(parts, clickEvents, hoverEvents);
    }

    public static StyledText fromString(String codedString) {
        return new StyledText(StyledTextPart.fromCodedString(codedString, Style.EMPTY, (StyledText)null, Style.EMPTY), List.of(), List.of());
    }

    public static StyledText fromUnformattedString(String unformattedString) {
        StyledTextPart part = new StyledTextPart(unformattedString, Style.EMPTY, (StyledText)null, Style.EMPTY);
        return new StyledText(List.of(part), List.of(), List.of());
    }

    public static StyledText fromPart(StyledTextPart part) {
        return new StyledText(List.of(part), List.of(), List.of());
    }

    public static StyledText fromJson(String json) {
        MutableText component = Serializer.fromJson(json);
        return component == null ? EMPTY : fromComponent(component);
    }

    public String getString(StyleType type) {
        StringBuilder builder = new StringBuilder();
        PartStyle previousStyle = null;

        StyledTextPart part;
        for(Iterator var4 = this.parts.iterator(); var4.hasNext(); previousStyle = part.getPartStyle()) {
            part = (StyledTextPart)var4.next();
            builder.append(part.getString(previousStyle, type));
        }

        return builder.toString();
    }

    public String getString() {
        return this.getString(StyleType.DEFAULT);
    }

    public String getStringWithoutFormatting() {
        return this.getString(StyleType.NONE);
    }

    public MutableText getComponent() {
        if (this.parts.isEmpty()) {
            return Text.empty();
        } else {
            MutableText component = ((StyledTextPart)this.parts.get(0)).getComponent();

            for(int i = 1; i < this.parts.size(); ++i) {
                component.append(((StyledTextPart)this.parts.get(i)).getComponent());
            }

            return component;
        }
    }

    public int length() {
        return this.parts.stream().mapToInt(StyledTextPart::length).sum();
    }

    public static StyledText join(StyledText styledTextSeparator, StyledText... texts) {
        List<StyledTextPart> parts = new ArrayList();
        List<ClickEvent> clickEvents = new ArrayList();
        List<HoverEvent> hoverEvents = new ArrayList();
        int length = texts.length;

        for(int i = 0; i < length; ++i) {
            StyledText text = texts[i];
            parts.addAll(text.parts);
            if (i != length - 1) {
                parts.addAll(styledTextSeparator.parts);
            }

            clickEvents.addAll(text.clickEvents);
            hoverEvents.addAll(text.hoverEvents);
        }

        clickEvents.addAll(styledTextSeparator.clickEvents);
        hoverEvents.addAll(styledTextSeparator.hoverEvents);
        return new StyledText(parts, clickEvents, hoverEvents);
    }

    public static StyledText join(StyledText styledTextSeparator, Iterable<StyledText> texts) {
        return join(styledTextSeparator, (StyledText[])Iterables.toArray(texts, StyledText.class));
    }

    public static StyledText join(String codedStringSeparator, StyledText... texts) {
        return join(fromString(codedStringSeparator), texts);
    }

    public static StyledText join(String codedStringSeparator, Iterable<StyledText> texts) {
        return join(fromString(codedStringSeparator), (StyledText[])Iterables.toArray(texts, StyledText.class));
    }

    public static StyledText concat(StyledText... texts) {
        List<StyledTextPart> parts = new ArrayList();
        List<ClickEvent> clickEvents = new ArrayList();
        List<HoverEvent> hoverEvents = new ArrayList();
        StyledText[] var4 = texts;
        int var5 = texts.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            StyledText text = var4[var6];
            parts.addAll(text.parts);
            clickEvents.addAll(text.clickEvents);
            hoverEvents.addAll(text.hoverEvents);
        }

        return new StyledText(parts, clickEvents, hoverEvents);
    }

    public static StyledText concat(Iterable<StyledText> texts) {
        return concat((StyledText[])Iterables.toArray(texts, StyledText.class));
    }

    public StyledText trim() {
        if (this.parts.isEmpty()) {
            return this;
        } else {
            List<StyledTextPart> newParts = new ArrayList(this.parts);
            newParts.set(0, ((StyledTextPart)newParts.get(0)).stripLeading());
            int lastIndex = newParts.size() - 1;
            newParts.set(lastIndex, ((StyledTextPart)newParts.get(lastIndex)).stripTrailing());
            return new StyledText(newParts, this.clickEvents, this.hoverEvents);
        }
    }

    public boolean isEmpty() {
        return this.parts.isEmpty();
    }

    public boolean isBlank() {
        return this.parts.stream().allMatch(StyledTextPart::isBlank);
    }

    public boolean contains(String codedString) {
        return this.contains(codedString, StyleType.DEFAULT);
    }

    public boolean contains(StyledText styledText) {
        return this.contains(styledText.getString(StyleType.DEFAULT), StyleType.DEFAULT);
    }

    public boolean contains(String codedString, StyleType styleType) {
        return this.getString(styleType).contains(codedString);
    }

    public boolean contains(StyledText styledText, StyleType styleType) {
        return this.contains(styledText.getString(styleType), styleType);
    }

    public boolean startsWith(String codedString) {
        return this.startsWith(codedString, StyleType.DEFAULT);
    }

    public boolean startsWith(StyledText styledText) {
        return this.startsWith(styledText.getString(StyleType.DEFAULT), StyleType.DEFAULT);
    }

    public boolean startsWith(String codedString, StyleType styleType) {
        return this.getString(styleType).startsWith(codedString);
    }

    public boolean startsWith(StyledText styledText, StyleType styleType) {
        return this.startsWith(styledText.getString(styleType), styleType);
    }

    public boolean endsWith(String codedString) {
        return this.endsWith(codedString, StyleType.DEFAULT);
    }

    public boolean endsWith(StyledText styledText) {
        return this.endsWith(styledText.getString(StyleType.DEFAULT), StyleType.DEFAULT);
    }

    public boolean endsWith(String codedString, StyleType styleType) {
        return this.getString(styleType).endsWith(codedString);
    }

    public boolean endsWith(StyledText styledText, StyleType styleType) {
        return this.endsWith(styledText.getString(styleType), styleType);
    }

    public Matcher getMatcher(Pattern pattern) {
        return this.getMatcher(pattern, StyleType.DEFAULT);
    }

    public Matcher getMatcher(Pattern pattern, StyleType styleType) {
        return pattern.matcher(this.getString(styleType));
    }

    public boolean matches(Pattern pattern) {
        return this.matches(pattern, StyleType.DEFAULT);
    }

    public boolean matches(Pattern pattern, StyleType styleType) {
        return pattern.matcher(this.getString(styleType)).matches();
    }

    public boolean find(Pattern pattern) {
        return this.find(pattern, StyleType.DEFAULT);
    }

    public boolean find(Pattern pattern, StyleType styleType) {
        return pattern.matcher(this.getString(styleType)).find();
    }

    public StyledText append(StyledText styledText) {
        return concat(this, styledText);
    }

    public StyledText append(String codedString) {
        return this.append(fromString(codedString));
    }

    public StyledText appendPart(StyledTextPart part) {
        List<StyledTextPart> newParts = new ArrayList(this.parts);
        newParts.add(part);
        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText prepend(StyledText styledText) {
        return concat(styledText, this);
    }

    public StyledText prepend(String codedString) {
        return this.prepend(fromString(codedString));
    }

    public StyledText prependPart(StyledTextPart part) {
        List<StyledTextPart> newParts = new ArrayList(this.parts);
        newParts.add(0, part);
        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText[] split(String regex) {
        if (this.parts.isEmpty()) {
            return new StyledText[]{EMPTY};
        } else {
            Pattern pattern = Pattern.compile(regex);
            List<StyledText> splitTexts = new ArrayList();
            List<StyledTextPart> splitParts = new ArrayList();

            for(int i = 0; i < this.parts.size(); ++i) {
                StyledTextPart part = (StyledTextPart)this.parts.get(i);
                String partString = part.getString((PartStyle)null, StyleType.NONE);
                int maxSplit = i == this.parts.size() - 1 ? 0 : -1;
                List<String> stringParts = Arrays.stream(pattern.split(partString, maxSplit)).toList();
                Matcher matcher = pattern.matcher(partString);
                if (matcher.find()) {
                    for(int j = 0; j < stringParts.size(); ++j) {
                        String stringPart = (String)stringParts.get(j);
                        splitParts.add(new StyledTextPart(stringPart, part.getPartStyle().getStyle(), (StyledText)null, Style.EMPTY));
                        if (j != stringParts.size() - 1) {
                            splitTexts.add(new StyledText(splitParts, this.clickEvents, this.hoverEvents));
                            splitParts.clear();
                        }
                    }
                } else {
                    splitParts.add(part);
                }
            }

            if (!splitParts.isEmpty()) {
                splitTexts.add(new StyledText(splitParts, this.clickEvents, this.hoverEvents));
            }

            return (StyledText[])splitTexts.toArray(new StyledText[0]);
        }
    }

    public StyledText substring(int beginIndex) {
        return this.substring(beginIndex, this.length());
    }

    public StyledText substring(int beginIndex, int endIndex) {
        if (endIndex < beginIndex) {
            throw new IndexOutOfBoundsException("endIndex must be greater than beginIndex");
        } else if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("beginIndex must be greater than or equal to 0");
        } else if (endIndex > this.length()) {
            throw new IndexOutOfBoundsException("endIndex must be less than or equal to length()");
        } else {
            List<StyledTextPart> includedParts = new ArrayList();
            int currentIndex = 0;

            StyledTextPart part;
            for(Iterator var5 = this.parts.iterator(); var5.hasNext(); currentIndex += part.length()) {
                part = (StyledTextPart)var5.next();
                if (currentIndex >= beginIndex && currentIndex + part.length() < endIndex) {
                    includedParts.add(part);
                } else if (currentIndex + part.length() >= beginIndex || currentIndex + part.length() > endIndex) {
                    int startIndexInPart = Math.max(0, beginIndex - currentIndex);
                    int endIndexInPart = Math.min(part.length(), endIndex - currentIndex);
                    String includedSubstring = part.getString((PartStyle)null, StyleType.NONE).substring(startIndexInPart, endIndexInPart);
                    includedParts.add(new StyledTextPart(includedSubstring, part.getPartStyle().getStyle(), (StyledText)null, Style.EMPTY));
                }
            }

            return new StyledText(includedParts, this.clickEvents, this.hoverEvents);
        }
    }

    public StyledText replaceFirst(String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        List<StyledTextPart> newParts = new ArrayList();
        Iterator var5 = this.parts.iterator();

        while(var5.hasNext()) {
            StyledTextPart part = (StyledTextPart)var5.next();
            String partString = part.getString((PartStyle)null, StyleType.NONE);
            Matcher matcher = pattern.matcher(partString);
            if (matcher.find()) {
                String replacedString = matcher.replaceFirst(replacement);
                newParts.add(new StyledTextPart(replacedString, part.getPartStyle().getStyle(), (StyledText)null, Style.EMPTY));
                newParts.addAll(this.parts.subList(this.parts.indexOf(part) + 1, this.parts.size()));
                break;
            }

            newParts.add(part);
        }

        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText replaceAll(String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        List<StyledTextPart> newParts = new ArrayList();
        Iterator var5 = this.parts.iterator();

        while(var5.hasNext()) {
            StyledTextPart part = (StyledTextPart)var5.next();
            String partString = part.getString((PartStyle)null, StyleType.NONE);
            Matcher matcher = pattern.matcher(partString);
            if (matcher.find()) {
                String replacedString = matcher.replaceAll(replacement);
                newParts.add(new StyledTextPart(replacedString, part.getPartStyle().getStyle(), (StyledText)null, Style.EMPTY));
            } else {
                newParts.add(part);
            }
        }

        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText[] getPartsAsTextArray() {
        return (StyledText[])this.parts.stream().map(StyledText::fromPart).toArray((x$0) -> {
            return new StyledText[x$0];
        });
    }

    public StyledText iterate(BiFunction<StyledTextPart, List<StyledTextPart>, IterationDecision> function) {
        List<StyledTextPart> newParts = new ArrayList();

        for(int i = 0; i < this.parts.size(); ++i) {
            StyledTextPart part = (StyledTextPart)this.parts.get(i);
            List<StyledTextPart> functionParts = new ArrayList();
            functionParts.add(part);
            IterationDecision decision = (IterationDecision)function.apply(part, functionParts);
            newParts.addAll(functionParts);
            if (decision == IterationDecision.BREAK) {
                newParts.addAll(this.parts.subList(i + 1, this.parts.size()));
                break;
            }
        }

        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText iterateBackwards(BiFunction<StyledTextPart, List<StyledTextPart>, IterationDecision> function) {
        List<StyledTextPart> newParts = new ArrayList();

        for(int i = this.parts.size() - 1; i >= 0; --i) {
            StyledTextPart part = (StyledTextPart)this.parts.get(i);
            List<StyledTextPart> functionParts = new ArrayList();
            functionParts.add(part);
            IterationDecision decision = (IterationDecision)function.apply(part, functionParts);
            newParts.addAll(0, functionParts);
            if (decision == IterationDecision.BREAK) {
                newParts.addAll(0, this.parts.subList(0, i));
                break;
            }
        }

        return new StyledText(newParts, this.clickEvents, this.hoverEvents);
    }

    public StyledText withoutFormatting() {
        return this.iterate((part, functionParts) -> {
            functionParts.set(0, new StyledTextPart(part.getString((PartStyle)null, StyleType.NONE), Style.EMPTY, (StyledText)null, Style.EMPTY));
            return IterationDecision.CONTINUE;
        });
    }

    public boolean equalsString(String string) {
        return this.equalsString(string, StyleType.DEFAULT);
    }

    public boolean equalsString(String string, StyleType styleType) {
        return this.getString(styleType).equals(string);
    }

    public StyledTextPart getFirstPart() {
        return this.parts.isEmpty() ? null : (StyledTextPart)this.parts.get(0);
    }

    public StyledTextPart getLastPart() {
        return this.parts.isEmpty() ? null : (StyledTextPart)this.parts.get(this.parts.size() - 1);
    }

    public int getPartCount() {
        return this.parts.size();
    }

    int addClickEvent(ClickEvent clickEvent) {
        for(int i = 0; i < this.clickEvents.size(); ++i) {
            ClickEvent event = (ClickEvent)this.clickEvents.get(i);
            if (event.equals(clickEvent)) {
                return i + 1;
            }
        }

        this.clickEvents.add(clickEvent);
        return this.clickEvents.size();
    }

    int addHoverEvent(HoverEvent hoverEvent) {
        for(int i = 0; i < this.hoverEvents.size(); ++i) {
            HoverEvent event = (HoverEvent)this.hoverEvents.get(i);
            if (event.equals(hoverEvent)) {
                return i + 1;
            }
        }

        this.hoverEvents.add(hoverEvent);
        return this.hoverEvents.size();
    }

    private StyledTextPart getPartBefore(StyledTextPart part) {
        int index = this.parts.indexOf(part);
        return index == 0 ? null : (StyledTextPart)this.parts.get(index - 1);
    }

    public Iterator<StyledTextPart> iterator() {
        return this.parts.iterator();
    }

    public String toString() {
        return "StyledText{'" + this.getString(StyleType.INCLUDE_EVENTS) + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            StyledText that = (StyledText)o;
            return Objects.deepEquals(this.parts, that.parts) && Objects.deepEquals(this.clickEvents, that.clickEvents) && Objects.deepEquals(this.hoverEvents, that.hoverEvents);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.parts, this.clickEvents, this.hoverEvents});
    }
}
