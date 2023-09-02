package me.glxphs.gmod.config

import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory

@ConfigCategory(
    "General",
    description = "General configuration options",
    order = 0
)
object GeneralConfig {
    @ConfigKey(
        "Overlays: Outlined Text",
        description = "If enabled, overlay text will have a black outline.",
    )
    val outlinedText = ConfigValue(false)
}