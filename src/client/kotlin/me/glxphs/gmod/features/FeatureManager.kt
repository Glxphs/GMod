package me.glxphs.gmod.features

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.features.impl.GambleReminderFeature
import me.glxphs.gmod.features.impl.MythicWeightTooltipFeature
import me.glxphs.gmod.features.impl.SpellMacroFeature
import me.glxphs.gmod.features.impl.UnidMythicPcTooltipFeature
import me.glxphs.gmod.features.impl.hud.OverlayFeature
import me.glxphs.gmod.features.impl.hud.LootrunOverlay
import me.glxphs.gmod.features.impl.hud.SpellClicksOverlay
import me.glxphs.gmod.features.impl.hud.GambleReminderOverlay
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object FeatureManager {
    val features = mutableListOf(
        SpellMacroFeature,

        MythicWeightTooltipFeature,
        UnidMythicPcTooltipFeature,

        GambleReminderFeature,

        SpellClicksOverlay,
        LootrunOverlay,
        GambleReminderOverlay
    )

    fun getHudFeatures(): List<OverlayFeature> {
        return features.filterIsInstance<OverlayFeature>()
    }

    fun registerFeatures() {
        features.forEach {
            it.onInitialize()
        }
    }

    fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        features.forEach {
            it.registerCommands(dispatcher)
        }
    }

    fun getFeature(name: String): Feature {
        return features.first { it.name == name }
    }

    fun getHudFeature(name: String): OverlayFeature {
        return getHudFeatures().first { it.name == name }
    }
}