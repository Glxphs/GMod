package me.glxphs.gmod.features

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.config.ConfigManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

abstract class Feature(val name: String) {
    open fun onInitialize() {
        ConfigManager.registerConfig(this)
    }

    open fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {}
}