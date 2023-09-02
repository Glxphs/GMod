package me.glxphs.gmod.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

abstract class Command {
    abstract fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>)
}