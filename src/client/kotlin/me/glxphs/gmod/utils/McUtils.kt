package me.glxphs.gmod.utils

import net.minecraft.client.MinecraftClient

class McUtils {
    companion object {
        val mc: MinecraftClient
            get() = MinecraftClient.getInstance()
    }
}