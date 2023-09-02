//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.glxphs.gmod.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.LinkedList;

public final class LoreUtils {

    public static LinkedList<StyledText> getLore(ItemStack itemStack) {
        NbtList loreTag = getLoreTag(itemStack);
        LinkedList<StyledText> lore = new LinkedList();
        if (loreTag == null) {
            return lore;
        } else {
            for(int i = 0; i < loreTag.size(); ++i) {
                lore.add(StyledText.fromJson(loreTag.getString(i)));
            }

            return lore;
        }
    }

    public static NbtList getLoreTag(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        } else {
            NbtCompound display = itemStack.getSubNbt("display");
            if (display != null && display.getNbtType() == NbtCompound.TYPE && display.contains("Lore")) {
                NbtElement loreBase = display.get("Lore");
                return loreBase.getNbtType() != NbtList.TYPE ? null : (NbtList)loreBase;
            } else {
                return null;
            }
        }
    }
}
