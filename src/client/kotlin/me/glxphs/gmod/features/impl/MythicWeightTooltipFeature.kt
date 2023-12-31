package me.glxphs.gmod.features.impl

import com.wynntils.core.components.Models
import com.wynntils.models.gear.type.GearTier
import com.wynntils.models.items.items.game.GearItem
import com.wynntils.models.stats.StatCalculator
import com.wynntils.utils.wynn.ColorScaleUtils
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory
import me.glxphs.gmod.events.SlotRenderCallback
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.features.MythicWeightsLoader
import me.glxphs.gmod.utils.ColorUtils
import me.glxphs.gmod.utils.McUtils
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@ConfigCategory(
    name = "Mythic Weighed Overall Tooltip",
    description = "Shows the weighed overall of your mythic gear.",
)
object MythicWeightTooltipFeature : Feature("Mythic Weighed Overall Tooltip") {
    @ConfigKey(
        name = "Weighing Mode",
        description = "The mode to weigh your gear. 'normal' is the default. Others: 'war, lootrun, spell, arcanist, melee, thunder'.",
        order = 1
    )
    var weighingMode = ConfigValue("normal")

    @ConfigKey(
        name = "Item Weight Overlay",
        description = "Adds an overlay to your items showing their weighed overall.",
        order = 2
    )
    var itemWeightOverlay = ConfigValue(value = true)

    @ConfigKey(
        name = "Overlay In Trade Market Only",
        description = "Only shows the item weight overlay in the trade market.",
        order = 3
    )
    var tradeMarketOnly = ConfigValue(value = true)

    override fun onInitialize() {
        super.onInitialize()
        ItemTooltipCallback.EVENT.register { stack, context, tooltip ->
            if (!enabled.value) return@register

            val weightedOverall = getWeightedOverall(stack) ?: return@register
            val overallString = ColorScaleUtils.getPercentageTextComponent(weightedOverall.toFloat(), true, 2);

            tooltip.add(1, Text.literal("Weighted Overall:").formatted(Formatting.GRAY).append(overallString))
        }
        SlotRenderCallback.EVENT.register { matrixStack, screen, slot ->
            if (!enabled.value) return@register
            if (!itemWeightOverlay.value) return@register

            if (tradeMarketOnly.value && !screen.title.string.contains("Trade Market") && !screen.title.string.contains("Search Results")) {
                return@register
            }

            val itemStack = slot.stack ?: return@register
            if (itemStack.isEmpty) return@register

            val weightedOverall = getWeightedOverall(itemStack) ?: return@register

            val overallString = ColorUtils.getPercentageTextComponent(weightedOverall.toFloat(), true, 0)

            // draw text
            val textRenderer = McUtils.mc.textRenderer

            matrixStack.push()
            matrixStack.translate(0.0f, 0.0f, 300.0f)
            val xOffset = (18 - textRenderer.getWidth(overallString))
            val scaledX = slot.x + xOffset
            val scaledY = slot.y
            DrawableHelper.drawTextWithShadow(matrixStack, textRenderer, overallString, scaledX, scaledY, 0xFFFFFF)
            matrixStack.pop()
        }
    }

    fun getWeightedOverall(stack: ItemStack): Double? {
        val weights = MythicWeightsLoader.getWeightsMap()
        if (weights.isEmpty()) return null
        val gearItemOpt = Models.Item.asWynnItem(stack, GearItem::class.java)
        if (gearItemOpt.isEmpty) return null
        val gearItem = gearItemOpt.get()
        val gearInstanceOpt = gearItem.gearInstance
        if (gearInstanceOpt.isEmpty) return null
        val gearInstance = gearInstanceOpt.get()
        val gearInfo = gearItem.gearInfo
        if (gearItem.gearTier != GearTier.MYTHIC) return null

        val mode = if (weighingMode.value.lowercase() == "normal") "" else " (${weighingMode.value})"
        val itemName = "${gearInfo.name}$mode"

        val weightMap = weights.filter {
            it.key.lowercase() == itemName.lowercase()
        }.values.firstOrNull() ?: return null

        var weightedOverall = 0.0

        for ((key, value) in weightMap) {
            val actual = gearInstance.identifications.find {
                it.statType.apiName == key
            } ?: continue
            val possible = gearInfo.getPossibleValues(actual.statType)
            val percentage = StatCalculator.getPercentage(actual, possible)

            weightedOverall += percentage * (value / 100.0)
        }
        return weightedOverall
    }
}