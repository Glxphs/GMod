package me.glxphs.gmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

public interface PlayerSwingHandCallback {
    Event<PlayerSwingHandCallback> EVENT = EventFactory.createArrayBacked(PlayerSwingHandCallback.class, (listeners) -> (player, hand) -> {
        for (PlayerSwingHandCallback listener : listeners) {
            listener.onPlayerSwingHand(player, hand);
        }
    });

    void onPlayerSwingHand(ClientPlayerEntity player, Hand hand);
}
