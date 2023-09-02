package me.glxphs.gmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public interface ChatReceivedCallback {
    Event<ChatReceivedCallback> EVENT = EventFactory.createArrayBacked(ChatReceivedCallback.class, (listeners) -> (message, overlay) -> {
        for (ChatReceivedCallback listener : listeners) {
            listener.onChatReceived(message, overlay);
        }
    });

    void onChatReceived(Text message, boolean overlay);
}
