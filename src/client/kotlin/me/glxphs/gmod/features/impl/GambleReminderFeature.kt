package me.glxphs.gmod.features.impl

import com.wynntils.core.components.Models
import com.wynntils.core.text.StyledText
import com.wynntils.models.gear.type.GearTier
import com.wynntils.models.items.items.game.GearItem
import com.wynntils.models.stats.StatCalculator
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory
import me.glxphs.gmod.events.InventoryCallback
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.features.MythicWeightsLoader
import me.glxphs.gmod.utils.McUtils
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@ConfigCategory(
    "Gamble Reminder",
    description = "A reminder to gamble every day",
)
object GambleReminderFeature : Feature("Gamble Reminder") {
    @ConfigKey(
        "Bad Stat Reroll Reminder",
        description = "Reminds you to reroll the item if one of the stats is bad.",
        order = 1
    )
    var badStatRerollReminder = ConfigValue(true)

    @ConfigKey(
        "Lootrun Reward Reroll Reminder",
        description = "Reminds you to reroll the lootrun reward if there are no/cheap mythics.",
        order = 2
    )
    var lootrunRewardRerollReminder = ConfigValue(true)

    var acceptItemStack: ItemStack? = null
    var rerollItemStack: ItemStack? = null

    var mythicCount = 0
    var totalPrice = 0.0
    var mythics = mutableListOf<GearItem>()

    const val REWARD_CHEST_TITLE = "§f\uE000\uE079"

    fun resetMythicCounter() {
        mythicCount = 0
        mythics.clear()

        totalPrice = 0.0

        acceptItemStack = null
        rerollItemStack = null
    }

    override fun onInitialize() {
        super.onInitialize()
        InventoryCallback.EVENT.register { syncId, revision, contents, cursorStack ->
            val screen = McUtils.mc.currentScreen ?: return@register

            val title = StyledText.fromComponent(screen.title).string

            println("$title $REWARD_CHEST_TITLE ${title == REWARD_CHEST_TITLE}")

            if (title != REWARD_CHEST_TITLE) return@register

            resetMythicCounter()

            rerollItemStack = contents[5]
            acceptItemStack = contents[3]

            val unidPrices = MythicWeightsLoader.getUnidMap()

            contents.subList(0, 54).forEach {
                val gearItemOpt = Models.Item.asWynnItem(it, GearItem::class.java)
                if (gearItemOpt.isEmpty) return@forEach
                val gearItem = gearItemOpt.get()

                if (gearItem.gearTier == GearTier.MYTHIC) {
                    mythicCount++
                    mythics.add(gearItem)
                }
            }
            totalPrice = mythics.sumOf {
                unidPrices[it.gearInfo.name]?.toDouble() ?: 0.0
            }
        }
        ItemTooltipCallback.EVENT.register { stack, context, tooltip ->
            if (!enabled.value) return@register

            if (lootrunRewardRerollReminder.value) {
                if (mythicCount == 0) {
                    if (stack.equals(acceptItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("Do NOT accept the rewards!")
                                    .formatted(Formatting.RED),
                                Text.literal("There are NO mythics in this chest!")
                                    .formatted(Formatting.RED),
                                Text.literal("Reroll and you will have a chance to get mythics!")
                                    .formatted(Formatting.RED),
                                Text.literal("If you do not gamble, you will not get good gear!")
                                    .formatted(Formatting.RED)
                            )
                        )
                    }
                    else if (stack.equals(rerollItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("CLICK this reroll button!")
                                    .formatted(Formatting.RED),
                                Text.literal("There are NO mythics in this chest!")
                                    .formatted(Formatting.RED),
                                Text.literal("Reroll and you will have a chance to get mythics!")
                                    .formatted(Formatting.RED),
                                Text.literal("If you do not gamble, you will not get good gear!")
                                    .formatted(Formatting.RED)
                            )
                        )
                    }
                } else if (totalPrice < 5) {
                    if (stack.equals(acceptItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("Do NOT accept the rewards!")
                                    .formatted(Formatting.RED),
                                Text.literal("You only have $mythicCount mythic(s),")
                                    .formatted(Formatting.RED),
                                Text.literal("which is worth ${totalPrice}stx only!")
                                    .formatted(Formatting.RED),
                                Text.literal("Reroll and you might get mythics worth more!")
                                    .formatted(Formatting.RED),
                                Text.literal("If you do not gamble, you will not get good gear!")
                                    .formatted(Formatting.RED)
                            )
                        )
                    }
                    else if (stack.equals(rerollItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("CLICK this reroll button!")
                                    .formatted(Formatting.RED),
                                Text.literal("You only have $mythicCount mythic(s),")
                                    .formatted(Formatting.RED),
                                Text.literal("which is worth ${totalPrice}stx only!")
                                    .formatted(Formatting.RED),
                                Text.literal("Reroll and you might get mythics worth more!")
                                    .formatted(Formatting.RED),
                                Text.literal("If you do not gamble, you will not get good gear!")
                                    .formatted(Formatting.RED)
                            )
                        )
                    }
                } else {
                    if (stack.equals(acceptItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("GOOD JOB ON GAMBLING!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("You have $mythicCount mythic(s),")
                                    .formatted(Formatting.GREEN),
                                Text.literal("which is worth ${totalPrice}stx!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("Accept the rewards!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("KEEP gambling and you will get more mythics!")
                                    .formatted(Formatting.GREEN)
                            )
                        )
                    }
                    else if (stack.equals(rerollItemStack)) {
                        tooltip.addAll(
                            1,
                            listOf(
                                Text.literal("GOOD JOB ON GAMBLING!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("You have $mythicCount mythic(s),")
                                    .formatted(Formatting.GREEN),
                                Text.literal("which is worth ${totalPrice}stx!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("Accept the rewards!")
                                    .formatted(Formatting.GREEN),
                                Text.literal("KEEP gambling and you will get more mythics!")
                                    .formatted(Formatting.GREEN)
                            )
                        )
                    }
                }
            }

            val weights = MythicWeightsLoader.getWeightsMap()

            if (weights.isEmpty()) return@register
            val gearItemOpt = Models.Item.asWynnItem(stack, GearItem::class.java)
            if (gearItemOpt.isEmpty) return@register
            val gearItem = gearItemOpt.get()

            val gearInstanceOpt = gearItem.gearInstance
            if (gearInstanceOpt.isEmpty) {
                tooltip.add(
                    1,
                    Text.literal("If you do not gamble, you will not get good gear!").formatted(
                        Formatting.RED)
                )
                tooltip.add(
                    1,
                    Text.literal("Roll the unidentified item right now!").formatted(
                        Formatting.RED)
                )
                return@register
            }
            val gearInstance = gearInstanceOpt.get()
            val gearInfo = gearItem.gearInfo

            if (gearItem.gearTier != GearTier.MYTHIC) return@register

            val rerolls = gearInstance.rerolls

            val mode = if (MythicWeightTooltipFeature.weighingMode.value.lowercase() == "normal") "" else " (${MythicWeightTooltipFeature.weighingMode.value})"
            val itemName = "${gearInfo.name}$mode"

            val weightMap = weights.filter {
                it.key.lowercase() == itemName.lowercase()
            }.values.firstOrNull() ?: return@register

            var weightedOverall = 0.0

            for ((key, value) in weightMap) {
                val actual = gearInstance.identifications.find {
                    it.statType.apiName == key
                } ?: continue
                val possible = gearInfo.getPossibleValues(actual.statType)
                val percentage = StatCalculator.getPercentage(actual, possible)

                weightedOverall += percentage * (value / 100.0)
            }

            if (weightedOverall < 90) {
                tooltip.add(
                    1,
                    Text.literal("If you do not gamble, you will not get good gear!").formatted(
                        Formatting.RED)
                )
                tooltip.add(
                    1,
                    Text.literal("This item is shit! Reroll to [${rerolls + 1}] right now!").formatted(
                        Formatting.RED)
                )
            } else {
                tooltip.add(
                    1,
                    Text.literal("This item is GOOD! Continue gambling!").formatted(
                        Formatting.GREEN)
                )
            }

            if (badStatRerollReminder.value) {
                gearInstance.identifications.forEach {
                    val percentage = StatCalculator.getPercentage(it, gearInfo.getPossibleValues(it.statType))
                    if (percentage < 60) {
                        tooltip.add(
                            1,
                            Text.literal("Bad ${it.statType.displayName}! Reroll right now!").formatted(
                                Formatting.RED)
                        )
                    }
                }
            }
        }
    }
}