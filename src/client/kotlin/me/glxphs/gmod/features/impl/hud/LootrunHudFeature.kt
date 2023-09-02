package me.glxphs.gmod.features.impl.hud

import com.wynntils.core.components.Models
import com.wynntils.models.lootrun.type.LootrunningState
import com.wynntils.utils.type.CappedValue
import me.glxphs.gmod.config.Config
import me.glxphs.gmod.config.ConfigEntry
import me.glxphs.gmod.config.RegisterConfig
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@RegisterConfig("Lootrun HUD")
object LootrunHudFeature : HudFeature("Lootrun HUD") {
    @ConfigEntry("Enabled")
    override var enabled: Config<Boolean> = Config(true, hidden = false, order = 0)

    @ConfigEntry("Hud Size")
    override var hudSize: Config<Float> = Config(1.0f, true)

    @ConfigEntry("X Position")
    override var x: Config<Float> = Config(100.0f, true)

    @ConfigEntry("Y Position")
    override var y: Config<Float> = Config(200.0f, true)

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