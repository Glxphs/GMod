package me.glxphs.gmod

import me.glxphs.gmod.commands.CommandManager
import me.glxphs.gmod.config.ConfigManager
import me.glxphs.gmod.config.GeneralConfig
import me.glxphs.gmod.config.KeyHandler
import me.glxphs.gmod.features.FeatureManager
import me.glxphs.gmod.features.MythicWeightsLoader
import me.glxphs.gmod.features.impl.hud.HudRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting


object GModClient : ClientModInitializer {
    override fun onInitializeClient() {
        MythicWeightsLoader.fetchWeights()

        FeatureManager.registerFeatures()
        ConfigManager.registerConfig(GeneralConfig)

        KeyHandler.registerKeybindings()
        HudRenderer.registerEvents()

        // Register client commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            FeatureManager.registerCommands(dispatcher)
            CommandManager.registerCommands(dispatcher)
        }
        ConfigManager.loadConfig()
    }

    fun log(message: String) {
        println("[GMod] $message")
    }

    fun inGameLog(message: String) {
        MinecraftClient.getInstance().inGameHud?.chatHud?.addMessage(Text.literal("[GMod] $message").formatted(
            Formatting.GRAY))
    }
}