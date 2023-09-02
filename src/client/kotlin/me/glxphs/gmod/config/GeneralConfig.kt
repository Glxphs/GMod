package me.glxphs.gmod.config

import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory

@ConfigCategory(
    "General",
    description = "General configuration options",
    order = 0
)
object GeneralConfig {
    @ConfigKey("Outlined Text")
    val outlinedText = ConfigValue(false)
}