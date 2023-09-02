package me.glxphs.gmod.features

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.config.ConfigManager
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

abstract class Feature(val name: String) {
    @ConfigKey(
        name = "Enabled",
        description = "Enable or disable this feature",
        order = 0,
    )
    open var enabled = ConfigValue(true)

    open fun onInitialize() {
        ConfigManager.registerConfig(this)
    }

    open fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {}
}