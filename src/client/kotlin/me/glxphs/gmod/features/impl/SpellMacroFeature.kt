package me.glxphs.gmod.features.impl

import me.glxphs.gmod.GModClient
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.KeyHandler.cast1stSpell
import me.glxphs.gmod.config.KeyHandler.cast2ndSpell
import me.glxphs.gmod.config.KeyHandler.cast3rdSpell
import me.glxphs.gmod.config.KeyHandler.cast4thSpell
import me.glxphs.gmod.config.annotations.RegisterConfig
import me.glxphs.gmod.features.Feature
import me.glxphs.gmod.features.impl.hud.SpellClicksHudFeature
import me.glxphs.gmod.utils.LoreUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import java.util.*

@RegisterConfig("Spell Macro")
object SpellMacroFeature : Feature("Spell Macro") {
    @ConfigKey(name = "Safe Cast (Using Spell Clicks HUD)", order = 0)
    var safeCast = ConfigValue(true)

    @ConfigKey(name = "1st Spell CPS")
    var cps1 = ConfigValue(20.0)

    @ConfigKey(name = "2nd Spell CPS")
    var cps2 = ConfigValue(14.5)

    @ConfigKey(name = "3rd Spell CPS")
    var cps3 = ConfigValue(20.0)

    @ConfigKey(name = "4th Spell CPS")
    var cps4 = ConfigValue(14.5)

    private val spells = listOf(
        Triple(SpellUnit.PRIMARY, SpellUnit.SECONDARY, SpellUnit.PRIMARY),
        Triple(SpellUnit.PRIMARY, SpellUnit.PRIMARY, SpellUnit.PRIMARY),
        Triple(SpellUnit.PRIMARY, SpellUnit.SECONDARY, SpellUnit.SECONDARY),
        Triple(SpellUnit.PRIMARY, SpellUnit.PRIMARY, SpellUnit.SECONDARY),
    )

    private val spellKeybinds = mapOf(
        cast1stSpell to spells[0],
        cast2ndSpell to spells[1],
        cast3rdSpell to spells[2],
        cast4thSpell to spells[3],
    )

    private val pressed = mutableMapOf(
        cast1stSpell to false,
        cast2ndSpell to false,
        cast3rdSpell to false,
        cast4thSpell to false,
    )

    override fun onInitialize() {
        super.onInitialize()

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (!enabled.value) return@register
            if (MinecraftClient.getInstance().player == null) return@register
            spellKeybinds.forEach { (key, spell) ->
                if (key.isPressed && !pressed[key]!!) {
                    pressed[key] = true
                    castSpell(spell)
                } else if (!key.isPressed) {
                    pressed[key] = false
                }
            }
        }
    }

    private val timer = Timer()

    val queue = mutableListOf<ClickType>()

    private fun castSpell(spell: Triple<SpellUnit, SpellUnit, SpellUnit>) {
        if (queue.size != 0) {
            GModClient.inGameLog("You are already casting a spell!")
            return
        }
        val archer = isArcher(MinecraftClient.getInstance().player!!)
        queue.addAll(listOf(spell.first.getClick(archer), spell.second.getClick(archer), spell.third.getClick(archer)))

        val hudClicks = SpellClicksHudFeature.clicks

        if (safeCast.value && hudClicks.size in 1..2) {
            if (queue.subList(0, hudClicks.size) == hudClicks) {
                for (i in 0 until hudClicks.size) {
                    queue.removeFirst()
                }
            } else {
                GModClient.inGameLog("Incompatible spell is currently being casted.")
                queue.clear()
                return
            }
        }

        println(queue)

        val cps = when (spell) {
            spells[0] -> cps1.value
            spells[1] -> cps2.value
            spells[2] -> cps3.value
            spells[3] -> cps4.value
            else -> 0.0
        }
        val fixedCps = cps.coerceIn(1.0, 1000.0)

        timer.schedule(object : TimerTask() {
            var i = 0
            var cancelled = false
            override fun run() {
                if (cancelled) return

                val click = queue[i]
                click.run()
                i++
                if (i == queue.size) {
                    doCancel()
                }
            }

            fun doCancel() {
                queue.clear()
                cancelled = true
                cancel()
            }
        }, 0, (1000.0 / fixedCps).toLong())
    }

    fun isArcher(player: ClientPlayerEntity): Boolean {
        val itemStack = player.mainHandStack
        if (itemStack.isEmpty) return false
        val lore = LoreUtils.getLore(itemStack)

        return lore.find { it.string.contains("§7 Class Req: Archer/Hunter") } != null
    }

    enum class ClickType(val abbreviation: String, val run: () -> Unit) {
        LEFT("L", {
            MinecraftClient.getInstance().player?.swingHand(net.minecraft.util.Hand.MAIN_HAND)
        }),
        RIGHT("R", {
            MinecraftClient.getInstance().interactionManager?.interactItem(
                MinecraftClient.getInstance().player,
                net.minecraft.util.Hand.MAIN_HAND
            )
        })
    }

    enum class SpellUnit {
        PRIMARY,
        SECONDARY;

        fun getClick(archer: Boolean): ClickType {
            return if (archer) {
                when (this) {
                    PRIMARY -> ClickType.LEFT
                    SECONDARY -> ClickType.RIGHT
                }
            } else {
                when (this) {
                    PRIMARY -> ClickType.RIGHT
                    SECONDARY -> ClickType.LEFT
                }
            }
        }
    }
}