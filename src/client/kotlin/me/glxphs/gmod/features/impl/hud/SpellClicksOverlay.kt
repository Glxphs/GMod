package me.glxphs.gmod.features.impl.hud

import com.wynntils.core.components.Models
import com.wynntils.models.items.items.game.GearItem
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory
import me.glxphs.gmod.events.PlayerSwingHandCallback
import me.glxphs.gmod.features.impl.SpellMacroFeature
import me.glxphs.gmod.utils.ClickCounter
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult

@ConfigCategory(
    "Spell Clicks Overlay",
    description = "Shows the clicks you have done for your spells.",
)
object SpellClicksOverlay : OverlayFeature("Spell Clicks Overlay") {
    val clicks = mutableListOf<SpellMacroFeature.ClickType>()
    private var clearCountdown = 50

    @ConfigKey("Scale")
    override var scale = ConfigValue(3.0f)

    @ConfigKey("X Position", hidden = true)
    override var x = ConfigValue(100.0f)

    @ConfigKey("Y Position", hidden = true)
    override var y = ConfigValue(100.0f)

    private var lastSelectedSlot = 0
    private var clickCounter = ClickCounter()
//    private var serverClickCounter = ClickCounter()

    override fun onInitialize() {
        super.onInitialize()
        UseItemCallback.EVENT.register { player, _, _ ->
            if (!enabled.value) return@register TypedActionResult.pass(null)
            player ?: return@register TypedActionResult.pass(null)
            if (player !is ClientPlayerEntity) return@register TypedActionResult.pass(null)
            onUseItem(player)
            TypedActionResult.pass(null)
        }
        ClientTickEvents.END_CLIENT_TICK.register {
            val currSelectedSlot = it.player?.inventory?.selectedSlot ?: return@register
            if (currSelectedSlot != lastSelectedSlot) {
                onChangeSlot()
                lastSelectedSlot = currSelectedSlot
            }

            if (clearCountdown > 0) {
                clearCountdown--
                if (clearCountdown == 0) clicks.clear()
            }
        }
        PlayerSwingHandCallback.EVENT.register { player, hand ->
            if (!enabled.value) return@register
            onPlayerSwing(player, hand)
        }
        /*ChatReceivedCallback.EVENT.register { message, overlay ->
            if (!enabled.value) return@register
            onChatReceived(message, overlay)
        }*/
    }

    private fun onUseItem(player: ClientPlayerEntity) {
        player.mainHandStack ?: return
        val gearItemOpt = Models.Item.asWynnItem(player.mainHandStack, GearItem::class.java)
        if (gearItemOpt.isEmpty) {
            return
        }
        val isArcher = SpellMacroFeature.isArcher(player)
        if (isArcher) {
            if (clicks.isEmpty() || clicks.size == 3) return
        }
        addClick(SpellMacroFeature.ClickType.RIGHT)
        TypedActionResult.pass(null)
    }

    private fun getClicksText(clicksList: List<SpellMacroFeature.ClickType>): Text {
        val clicksText = Text.empty()
        clicksList.forEachIndexed { index, it ->
            clicksText.append(
                Text.literal(it.abbreviation)
                    .formatted(Formatting.GREEN)
            )
            if (index != 2) {
                clicksText.append(
                    Text.literal("-")
                        .formatted(Formatting.GRAY)
                )
            }
        }
        if (clicksList.size in 1..2) {
            repeat(3 - clicksList.size) {
                clicksText.append(
                    Text.literal("?")
                        .setStyle(Style.EMPTY.withUnderline(it == 0))
                        .formatted(Formatting.GRAY)
                )
                if (it != 2 - clicksList.size) {
                    clicksText.append(
                        Text.literal("-")
                            .formatted(Formatting.GRAY)
                    )
                }
            }
        }
        return clicksText
    }

    override fun getTextList(): List<Text> {
        return listOf(getClicksText(clicks.toList()))
    }

    override fun getPreviewTextList(): List<Text> {
        return listOf(getClicksText(listOf(SpellMacroFeature.ClickType.RIGHT, SpellMacroFeature.ClickType.LEFT)))
    }

    private fun onPlayerSwing(player: ClientPlayerEntity, hand: Hand) {
        player.mainHandStack ?: return
        if (hand != Hand.MAIN_HAND) {
            return
        }
        val gearItemOpt = Models.Item.asWynnItem(player.mainHandStack, GearItem::class.java)
        if (gearItemOpt.isEmpty) {
            return
        }
        val isArcher = SpellMacroFeature.isArcher(player)
        if (!isArcher) {
            if (clicks.isEmpty() || clicks.size == 3) return
        }
        addClick(SpellMacroFeature.ClickType.LEFT)
    }

    private fun onChangeSlot() {
        clicks.clear()
    }

    /*val spellRegex = Regex("(?:[LR?]-){2}[LR?]")
    var lastSpellMessage = ""

    private fun onChatReceived(message: Text, overlay: Boolean) {
        if (!overlay) return
        val unformattedString = StyledText.fromComponent(message).stringWithoutFormatting
        val spell = spellRegex.find(unformattedString)?.value ?: run {
            lastSpellMessage = ""
            return
        }
        if (spell == lastSpellMessage) return
        lastSpellMessage = spell
        serverClickCounter.onClick()
    }*/

    private fun addClick(clickType: SpellMacroFeature.ClickType) {
        clickCounter.onClick()

        if (clicks.size == 3) clicks.clear()
        clicks.add(clickType)

        val mc = MinecraftClient.getInstance()
        try {
            mc.send { mc.player?.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f) }
        } catch (_: IllegalStateException) {
        }

        clearCountdown = 50
    }
}