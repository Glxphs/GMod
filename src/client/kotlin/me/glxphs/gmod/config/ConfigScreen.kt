package me.glxphs.gmod.config

import com.mojang.blaze3d.systems.RenderSystem
import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.BackgroundPainter
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.util.Identifier

class ConfigScreen(val desc: GuiDescription) : CottonClientScreen(desc) {
    override fun close() {
        ConfigManager.saveConfig()
        super.close()
    }

    override fun init() {
        super.init()
        val background = Identifier("gmod", "book.png")
        val texture = client!!.textureManager.getTexture(background)!!
        description.rootPanel.setBackgroundPainter(
            BackgroundPainter { matrices, left, top, panel ->
                client ?: return@BackgroundPainter
//                texture.bindTexture()
                matrices.push()
                // allow draw alpha
                RenderSystem.enableBlend()
                RenderSystem.setShaderTexture(0, background)
                DrawableHelper.drawTexture(matrices, left, top, panel.width, panel.height, 0f, 0f, 612, 341, 612, 341)
                matrices.pop()
            }
        )
    }
}