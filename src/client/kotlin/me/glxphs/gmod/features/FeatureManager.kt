package me.glxphs.gmod.features

import com.mojang.brigadier.CommandDispatcher
import me.glxphs.gmod.features.impl.GambleReminderFeature
import me.glxphs.gmod.features.impl.MythicWeightTooltipFeature
import me.glxphs.gmod.features.impl.SpellMacroFeature
import me.glxphs.gmod.features.impl.UnidMythicPcTooltipFeature
import me.glxphs.gmod.features.impl.hud.HudFeature
import me.glxphs.gmod.features.impl.hud.LootrunHudFeature
import me.glxphs.gmod.features.impl.hud.SpellClicksHudFeature
import me.glxphs.gmod.features.impl.hud.TestHudFeature
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object FeatureManager {
    val features = mutableListOf(
        SpellMacroFeature,

        MythicWeightTooltipFeature,
        UnidMythicPcTooltipFeature,

        GambleReminderFeature,

        SpellClicksHudFeature,
        LootrunHudFeature,
        TestHudFeature
    )

    fun getHudFeatures(): List<HudFeature> {
        return features.filterIsInstance<HudFeature>()
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

    fun getHudFeature(name: String): HudFeature {
        return getHudFeatures().first { it.name == name }
    }
}