package me.glxphs.gmod.config

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

object KeyHandler {
    private const val category = "key.category.gmod.gmod"

    val cast1stSpell = KeyBinding("key.gmod.spell_0", GLFW.GLFW_KEY_Z, category)
    val cast2ndSpell = KeyBinding("key.gmod.spell_1", GLFW.GLFW_KEY_X, category)
    val cast3rdSpell = KeyBinding("key.gmod.spell_2", GLFW.GLFW_KEY_C, category)
    val cast4thSpell = KeyBinding("key.gmod.spell_3", GLFW.GLFW_KEY_V, category)

    fun registerKeybindings() {
        KeyBindingHelper.registerKeyBinding(cast1stSpell)
        KeyBindingHelper.registerKeyBinding(cast2ndSpell)
        KeyBindingHelper.registerKeyBinding(cast3rdSpell)
        KeyBindingHelper.registerKeyBinding(cast4thSpell)
    }
}