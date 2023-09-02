package me.glxphs.gmod.features.impl

import com.wynntils.core.components.Models
import com.wynntils.models.gear.type.GearTier
import com.wynntils.models.items.items.game.GearItem
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.RegisterConfig
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.features.MythicWeightsLoader
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@RegisterConfig(section = "Unid Mythic Price Tooltip")
object UnidMythicPcTooltipFeature : Feature("Unid Mythic Price Tooltip") {
    override fun onInitialize() {
        super.onInitialize()
        ItemTooltipCallback.EVENT.register { stack, context, tooltip ->
            if (!enabled.value) return@register

            val unidMap = MythicWeightsLoader.getUnidMap()
            if (unidMap.isEmpty()) return@register

            val gearItemOpt = Models.Item.asWynnItem(stack, GearItem::class.java)
            if (gearItemOpt.isEmpty) return@register
            val gearItem = gearItemOpt.get()

            val gearInfo = gearItem.gearInfo
            if (gearItem.gearTier != GearTier.MYTHIC) return@register

            if (!gearItem.isUnidentified) return@register

            val itemName = gearInfo.name

            val priceString = unidMap.filter {
                it.key.lowercase() == itemName.lowercase()
            }.values.firstOrNull() ?: return@register

            tooltip.add(1,
                Text.literal("Unid Mythic Price: ")
                    .formatted(Formatting.GRAY)
                    .append(
                        Text.literal(priceString)
                            .append("stx")
                            .formatted(Formatting.GREEN)
                    )
            )
        }
    }
}