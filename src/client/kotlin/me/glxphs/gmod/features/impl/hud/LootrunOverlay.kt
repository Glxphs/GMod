package me.glxphs.gmod.features.impl.hud

import com.wynntils.core.components.Models
import com.wynntils.models.lootrun.type.LootrunningState
import com.wynntils.utils.type.CappedValue
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.ConfigCategory
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@ConfigCategory(
    "Lootrun Overlay",
    description = "Counts how many of your lootrun challenges are from red beacons.",
)
object LootrunOverlay : OverlayFeature("Lootrun Overlay") {
    @ConfigKey("Scale")
    override var scale: ConfigValue<Float> = ConfigValue(1.0f)

    @ConfigKey("X Position", hidden = true)
    override var x: ConfigValue<Float> = ConfigValue(100.0f)

    @ConfigKey("Y Position", hidden = true)
    override var y: ConfigValue<Float> = ConfigValue(200.0f,)

    override fun getTextList(): List<Text> {
        val lootrunState = Models.Lootrun.state
        if (lootrunState == LootrunningState.NOT_RUNNING) return emptyList()

        val allChallenges = Models.Lootrun.challenges
        val redChallenges = Models.Lootrun.redBeaconTaskCount

        return listOf(
            getChallengesText(allChallenges, redChallenges)
        )
    }

    override fun getPreviewTextList(): List<Text> {
        return listOf(getChallengesText(CappedValue(2, 14), 4))
    }

    private fun getChallengesText(
        allChallenges: CappedValue,
        redChallenges: Int
    ): Text {
        val whiteChallenges = allChallenges.max - redChallenges

        return Text.literal("Challenges: ")
            .formatted(Formatting.GRAY)
            .append(
                Text.literal(allChallenges.current.toString())
                    .formatted(Formatting.WHITE)
                    .append(
                        Text.literal(" of ")
                            .formatted(Formatting.GRAY)
                    )
                    .append(
                        Text.literal(redChallenges.toString())
                            .formatted(Formatting.RED)
                            .append(
                                Text.literal("+")
                                    .formatted(Formatting.GRAY)
                            )
                            .append(
                                Text.literal(whiteChallenges.toString())
                                    .formatted(Formatting.WHITE)
                            )
                    )
            )
    }
}