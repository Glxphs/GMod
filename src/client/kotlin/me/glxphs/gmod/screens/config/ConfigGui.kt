package me.glxphs.gmod.screens.config

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.*
import me.glxphs.gmod.config.ConfigValue
import me.glxphs.gmod.config.ConfigManager.config
import me.glxphs.gmod.config.annotations.ConfigKey
import net.fabricmc.fabric.api.util.TriState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ConfigGui : LightweightGuiDescription() {
    val configPanel: WGridPanel = WGridPanel()
    private val configScrollPanel = WScrollPanel(configPanel)
    var selectedSection: ConfigSectionWidget? = null

    init {
        val root = WGridPanel()

        root.setSize(18 * 24,  18 * 12)
        root.setInsets(Insets(22, 100, 70, 100))

        setRootPanel(root)

        val box = WBox(Axis.VERTICAL)
        val sprite = WSprite(Identifier("gmod", "gmod.png"))
//        val label = WLabel(Text.literal("GMod Config"))
//            .setHorizontalAlignment(HorizontalAlignment.CENTER)
//            .setVerticalAlignment(VerticalAlignment.CENTER)
        box.add(sprite, 6*18, 2*18)
        box.verticalAlignment = VerticalAlignment.CENTER
        box.horizontalAlignment = HorizontalAlignment.CENTER
        root.add(box, 0, 0, 12, 2)

        // add a button for hud config
        val hudPositionsButton = WButton(Text.literal("Edit Hud Positions"))

        hudPositionsButton.setOnClick {
            val hudPositionScreen = HudPositionScreen()
            MinecraftClient.getInstance().setScreenAndRender(hudPositionScreen)
        }

        hudPositionsButton.alignment = HorizontalAlignment.CENTER

        root.add(hudPositionsButton, 24-6+1, 0, 6, 1)

        val sectionsPanel = WPlainPanel()

        var row = 0

        config.forEach { (section, entries) ->
            if (entries.all { it.key.hidden }) return@forEach

            val sectionWidget = ConfigSectionWidget(section, entries)
            sectionsPanel.add(sectionWidget, 0, row++ * 12, 12*18, 12)
        }

        val sectionsScrollPanel = WScrollPanel(sectionsPanel)
        sectionsScrollPanel.setScrollingHorizontally(TriState.FALSE)

        configScrollPanel.setScrollingHorizontally(TriState.FALSE)

        root.add(sectionsScrollPanel, 0, 2, 12, 10)
        root.add(configScrollPanel, 13, 1, 11, 11)

        root.validate(this)
    }

    inner class ConfigSectionWidget(val section: String, val entries: MutableMap<ConfigKey, ConfigValue<*>>) : WLabel(Text.literal(section)) {
        init {
            setHorizontalAlignment(HorizontalAlignment.CENTER)
            setVerticalAlignment(VerticalAlignment.CENTER)
        }

        override fun paint(matrices: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
            if (selectedSection == this || hoveredProperty().get()) {
                color = 0x777777
                darkmodeColor = 0x777777
            } else {
                color = DEFAULT_TEXT_COLOR
                darkmodeColor = DEFAULT_DARKMODE_TEXT_COLOR
            }
            super.paint(matrices, x, y, mouseX, mouseY)
        }

        override fun onClick(x: Int, y: Int, button: Int): InputResult {
            val children = configPanel.streamChildren().toList()
            children.forEach { configPanel.remove(it) }
            var row = 0
            val label = WLabel(Text.literal(section))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
            configPanel.add(label, 0, row++, 11, 1)
            entries.toList().sortedBy {
                it.first.order
            }.forEach { (key, value) ->
                if (key.hidden) return@forEach
                configPanel.add(ConfigEntryWidget(key, value), 0, row, 11, 2)
                row += 2
            }
            this@ConfigGui.rootPanel.validate(this@ConfigGui)
            configScrollPanel.layout()
            selectedSection = this
            return InputResult.PROCESSED
        }
    }

    class ConfigEntryWidget(configKey: ConfigKey, entry: ConfigValue<*>) : WGridPanel() {
        init {
            val label = WLabel(Text.literal(configKey.name))
//                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
            add(label, 0, 0, 12, 1)

            when (entry.value) {
                is Int -> {
                    val textField = WTextField()
                    textField.text = entry.value.toString()
                    textField.setTextPredicate { it.toIntOrNull() != null }
                    textField.setChangedListener {
                        entry.set(it.toInt())
                    }
                    add(textField, 0, 1, 6, 1)
                }

                is Float -> {
                    val textField = WTextField()
                    textField.text = entry.value.toString()
                    textField.setTextPredicate { it.toFloatOrNull() != null }
                    textField.setChangedListener {
                        entry.set(it.toFloat())
                    }
                    add(textField, 0, 1, 6, 1)
                }

                is Double -> {
                    val textField = WTextField()
                    textField.text = entry.value.toString()
                    textField.setTextPredicate { it.toDoubleOrNull() != null }
                    textField.setChangedListener {
                        entry.set(it.toDouble())
                    }
                    add(textField, 0, 1, 6, 1)
                }

                is String -> {
                    val textField = WTextField()
                    textField.text = entry.value.toString()
                    textField.setChangedListener {
                        entry.set(it)
                    }
                    add(textField, 0, 1, 6, 1)
                }

                is Boolean -> {
                    val toggleButton = WToggleButton()
                    toggleButton.toggle = entry.value as Boolean
                    toggleButton.label = Text.literal(if (toggleButton.toggle) "Yes" else "No")
                    toggleButton.setOnToggle {
                        entry.set(toggleButton.toggle)
                        toggleButton.label = Text.literal(if (toggleButton.toggle) "Yes" else "No")
                    }
                    add(toggleButton, 0, 1, 6, 1)
                }
            }
        }

    }
}