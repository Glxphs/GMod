package me.glxphs.gmod.commands.impl

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.commands.Command
import me.glxphs.gmod.config.ConfigGui
import me.glxphs.gmod.config.ConfigScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient

object ConfigCommand : Command() {
    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("gmod")
                .executes {
                    MinecraftClient.getInstance().send {
                        MinecraftClient.getInstance().setScreen(ConfigScreen(ConfigGui()))
                    }
                    1
                }
        )
    }
}