package me.glxphs.gmod.mixin.client;

import me.glxphs.gmod.events.PlayerSwingHandCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(
            method = "swingHand(Lnet/minecraft/util/Hand;)V",
            at = @At("HEAD")
    )
    private void swingHand(Hand hand, CallbackInfo info) {
        if ((Object) this instanceof ClientPlayerEntity) {
            PlayerSwingHandCallback.EVENT.invoker().onPlayerSwingHand((ClientPlayerEntity) (Object) this, hand);
        }
    }
}
