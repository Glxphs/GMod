package me.glxphs.gmod.mixin.client;

import me.glxphs.gmod.events.ChatReceivedCallback;
import me.glxphs.gmod.events.InventoryCallback;
import me.glxphs.gmod.events.OpenScreenCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private boolean isRenderThread() {
        return client.isOnThread();
    }

    @Inject(
            method = "onGameMessage",
            at = @At("HEAD")
    )
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo info) {
        if (!isRenderThread()) return;
        ChatReceivedCallback.EVENT.invoker().onChatReceived(packet.content(), packet.overlay());
    }

    // onInventory
    @Inject(
            method = "onInventory",
            at = @At("HEAD")
    )
    private void onInventory(InventoryS2CPacket packet, CallbackInfo info) {
        if (!isRenderThread()) return;
        InventoryCallback.EVENT.invoker().onInventory(packet.getSyncId(), packet.getRevision(), packet.getContents(), packet.getCursorStack());
    }

    // onOpenScreen
    @Inject(
            method = "onOpenScreen",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo info) {
        if (!isRenderThread()) return;
        ActionResult result = OpenScreenCallback.EVENT.invoker().onOpenScreen(packet.getSyncId(), packet.getScreenHandlerType(), packet.getName());
        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
