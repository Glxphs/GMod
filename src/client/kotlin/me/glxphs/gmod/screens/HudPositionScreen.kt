package me.glxphs.gmod.screens

import me.glxphs.gmod.config.ConfigGui
import me.glxphs.gmod.config.ConfigScreen
import me.glxphs.gmod.features.FeatureManager
import me.glxphs.gmod.screens.widgets.HudPositionWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class HudPositionScreen : Screen(Text.of("GMod Hud Positions")) {
    override fun init() {
        FeatureManager.getHudFeatures().forEach {
            addDrawableChild(HudPositionWidget(it))
        }
    }

    override fun close() {
        MinecraftClient.getInstance().setScreenAndRender(ConfigScreen(ConfigGui()))
    }
}