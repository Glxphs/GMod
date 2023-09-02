package me.glxphs.gmod.commands

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.commands.impl.ConfigCommand
import me.glxphs.gmod.commands.impl.PerfectCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object CommandManager {
    val commands = mutableListOf(
        ConfigCommand,
        PerfectCommand
    )

    fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        commands.forEach { it.register(dispatcher) }
    }
}