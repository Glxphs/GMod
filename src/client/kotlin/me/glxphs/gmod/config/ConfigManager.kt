package me.glxphs.gmod.config

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.glxphs.gmod.features.Feature
import net.fabricmc.loader.api.FabricLoader
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object ConfigManager {
    private val configFile = FabricLoader.getInstance().configDir.resolve("gmodConfig.json").toFile()
    var config = mutableMapOf<String, MutableMap<String, Config<*>>>()

    val gson = GsonBuilder().setPrettyPrinting().create()

    fun registerConfig(feature: Feature) {
        val registerConfig = feature::class.findAnnotation<RegisterConfig>() ?: return
        val section = registerConfig.section

        if (!config.containsKey(section)) {
            config[section] = mutableMapOf()
        }

        val fields = feature::class.memberProperties

        fields.forEach { field ->
            val configEntry = field.findAnnotation<ConfigEntry>() ?: return@forEach
            var name = configEntry.name

            if (name == "") {
                name = field.name
            }

            val value = field.getter.call(feature) as Config<*>

            config[section]!![name] = value
        }
    }

    fun saveConfig() {
        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        val jsonStr = gson.toJson(config)
        configFile.writeText(jsonStr)
    }

    fun loadConfig() {
        if (configFile.exists()) {
            val jsonStr = configFile.readText()
            val type = object : TypeToken<MutableMap<String, MutableMap<String, Config<*>>>>() {}.type

            // Convert JSON to object
            val newConfig: MutableMap<String, MutableMap<String, Config<*>>> = gson.fromJson(jsonStr, type) ?: run {
                configFile.delete()
                mutableMapOf()
            }

            // update config
            config.forEach { (section, entries) ->
                entries.forEach second@ { (name, entry) ->
                    val newValue = newConfig[section]?.get(name) ?: return@second
                    newValue.value?.let { entry.set(it) }
                }
            }
        }
    }
}