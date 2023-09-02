package me.glxphs.gmod.config

import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.RegisterConfig

@RegisterConfig("General")
object GeneralConfig {
    @ConfigKey("Outlined Text")
    val outlinedText = ConfigValue(false)
}