package me.glxphs.gmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.List;

public interface InventoryCallback {
    Event<InventoryCallback> EVENT = EventFactory.createArrayBacked(InventoryCallback.class, (listeners) -> (syncId, revision, contents, cursorStack) -> {
        for (InventoryCallback listener : listeners) {
            listener.onInventory(syncId, revision, contents, cursorStack);
        }
    });

    // private final int syncId;
    //    private final int revision;
    //    private final List<ItemStack> contents;
    //    private final ItemStack cursorStack;
    void onInventory(int syncId, int revision, List<ItemStack> contents, ItemStack cursorStack);
}
