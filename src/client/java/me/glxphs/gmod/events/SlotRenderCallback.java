package me.glxphs.gmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

public interface SlotRenderCallback {
    Event<SlotRenderCallback> EVENT = EventFactory.createArrayBacked(SlotRenderCallback.class, (listeners) -> (matrixStack, screen, slot) -> {
        for (SlotRenderCallback listener : listeners) {
            listener.onSlotRender(matrixStack, screen, slot);
        }
    });

    void onSlotRender(MatrixStack matrixStack, Screen screen, Slot slot);
}