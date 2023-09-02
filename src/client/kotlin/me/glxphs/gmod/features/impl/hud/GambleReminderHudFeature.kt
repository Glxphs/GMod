package me.glxphs.gmod.features.impl.hud

import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.RegisterConfig
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@RegisterConfig("Gamble Reminder HUD")
object GambleReminderHudFeature : HudFeature("Gamble Reminder HUD") {
    @ConfigKey("Hud Size", hidden = true)
    override var hudSize: ConfigValue<Float> = ConfigValue(1.0f)

    @ConfigKey("X Position", hidden = true)
    override var x: ConfigValue<Float> = ConfigValue(100.0f)

    @ConfigKey("Y Position", hidden = true)
    override var y: ConfigValue<Float> = ConfigValue(300.0f)

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