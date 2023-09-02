package me.glxphs.gmod.mixin.client;

import me.glxphs.gmod.events.SlotRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Inject(
            method = {"drawSlot"},
            at = {@At("RETURN")}
    )
    private void renderSlotPost(MatrixStack poseStack, Slot slot, CallbackInfo info) {
        SlotRenderCallback.EVENT.invoker().onSlotRender(poseStack, (Screen) (Object) this, slot);
    }
}
