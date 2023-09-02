package me.glxphs.gmod.features.impl.hud

import me.glxphs.gmod.config.Config
import me.glxphs.gmod.config.ConfigEntry
import me.glxphs.gmod.config.RegisterConfig
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@RegisterConfig("Lootrun HUD")
object TestHudFeature : HudFeature("Lootrun HUD") {
    @ConfigEntry("Enabled")
    override var enabled: Config<Boolean> = Config(true, hidden = false, order = 0)

    @ConfigEntry("Hud Size")
    override var hudSize: Config<Float> = Config(1.0f, true)

    @ConfigEntry("X Position")
    override var x: Config<Float> = Config(100.0f, true)

    @ConfigEntry("Y Position")
    override var y: Config<Float> = Config(300.0f, true)

    override fun getTextList(): List<Text> {
        return listOf(
            Text.literal("If you do not gamble, you will never get good gear!")
                .formatted(Formatting.AQUA),
            Text.literal("Remember to gamble every day!")
                .formatted(Formatting.AQUA)
        )
    }

    override fun getPreviewTextList(): List<Text> {
        return getTextList()
    }
}