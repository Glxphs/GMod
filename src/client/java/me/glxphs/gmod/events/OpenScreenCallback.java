package me.glxphs.gmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;

public interface OpenScreenCallback {
    Event<OpenScreenCallback> EVENT = EventFactory.createArrayBacked(OpenScreenCallback.class, (listeners) -> (syncId, type, name) -> {
        for (OpenScreenCallback listener : listeners) {
            ActionResult result = listener.onOpenScreen(syncId, type, name);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    // private final int syncId;
    //    private final int revision;
    //    private final List<ItemStack> contents;
    //    private final ItemStack cursorStack;
    ActionResult onOpenScreen(int syncId, ScreenHandlerType<?> type, Text name);
}
