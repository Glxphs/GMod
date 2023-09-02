package me.glxphs.gmod.commands.impl

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import com.wynntils.core.components.Models
import com.wynntils.models.gear.type.GearInfo
import com.wynntils.models.stats.type.StatListOrdering
import me.glxphs.gmod.GModClient
import me.glxphs.gmod.commands.Command
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.roundToInt


object PerfectCommand : Command() {
    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            literal("perfect")
                .then(argument("item", greedyString())
                    .executes { ctx ->
                        val player = ctx.source.player ?: return@executes 1

                        val itemName = getString(ctx, "item")
                        val gearInfo = Models.Gear.getGearInfoFromApiName(itemName) ?: return@executes 1

                        GModClient.ingameLog(itemName)

                        val encoded = toEncodedString(gearInfo)
                        if (encoded == "") return@executes 1

                        // Add to clipboard
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        val selection = StringSelection(encoded)
                        clipboard.setContents(selection, null)

                        // Send to chat
                        player.sendMessage(Text.literal(encoded))
                        1
                    }
                )
        )
    }

    private val START = String(Character.toChars(0xF5FF0))
    private val END = String(Character.toChars(0xF5FF1))
    private val SEPARATOR = String(Character.toChars(0xF5FF2))
    private val RANGE = "[" + String(Character.toChars(0xF5000)) + "-" + String(Character.toChars(0xF5F00)) + "]"
    private val ENCODED_PATTERN: Pattern = Pattern.compile(
        START + "(?<Name>.+?)" + SEPARATOR + "(?<Ids>"
                + RANGE + "*)(?:" + SEPARATOR + "(?<Powders>" + RANGE + "+))?(?<Rerolls>" + RANGE + ")" + END
    )
    private const val OFFSET = 0xF5000
    private const val ENCODE_NAME = false

    fun toEncodedString(gearInfo: GearInfo): String {
        val itemName = gearInfo.name()

        // We must use Legacy ordering for compatibility reasons
        val sortedStats = Models.Stat.getSortedStats(gearInfo, StatListOrdering.LEGACY)

        // name
        val encoded = StringBuilder(START)
        encoded.append(if (ENCODE_NAME) encodeString(itemName) else itemName)
        encoded.append(SEPARATOR)

        // ids
        for (statType in sortedStats) {
            val possibleValues = gearInfo.getPossibleValues(statType) ?: return "<mismatched stats: cannot encode item>"
            val actualValue = possibleValues.range.high
            if (possibleValues.isPreIdentified) continue
            var valueToEncode = if (abs(possibleValues.baseValue()) > 100) {
                // Express value as percent
                (actualValue * 100.0 / possibleValues.baseValue() - 30).roundToInt()
            } else {
                // Express value as raw value shifted so lowest possible is 0
                actualValue - possibleValues.range().low()
            }

            // stars
            val stars = if (possibleValues.range.low > 0) {
                3
            } else {
                0
            }

            // encode value + stars in one character
            encoded.append(encodeNumber(valueToEncode * 4 + stars))
        }

        // rerolls
        encoded.append(encodeNumber(1))
        encoded.append(END)
        return encoded.toString()
    }

    private fun encodeString(text: String): String? {
        val encoded = java.lang.StringBuilder()
        for (c in text.toCharArray()) {
            val value = c.code - 32 // offset by 32 to ignore ascii control characters
            encoded.append(String(Character.toChars(value + OFFSET))) // get encoded representation
        }
        return encoded.toString()
    }

    private fun encodeNumber(value: Int): String? {
        return String(Character.toChars(value + OFFSET))
    }
}