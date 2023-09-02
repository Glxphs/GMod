package me.glxphs.gmod.config

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.glxphs.gmod.config.annotations.ConfigKey
import me.glxphs.gmod.config.annotations.RegisterConfig
import me.glxphs.gmod.features.Feature
import net.fabricmc.loader.api.FabricLoader
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object ConfigManager {
    private val configFile = FabricLoader.getInstance().configDir.resolve("gmodConfig.json").toFile()
    var config = mutableMapOf<String, MutableMap<ConfigKey, ConfigValue<*>>>()

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun registerConfig(obj: Any) {
        val registerConfig = obj::class.findAnnotation<RegisterConfig>() ?: return
        val section = registerConfig.section

        if (!config.containsKey(section)) {
            config[section] = mutableMapOf()
        }

        val fields = obj::class.memberProperties

        fields.forEach { field ->
            val configKey = field.findAnnotation<ConfigKey>() ?: return@forEach
            var name = configKey.name

            if (name == "") {
                name = field.name
            }

            val value = field.getter.call(obj) as ConfigValue<*>

            config[section]!![configKey] = value
        }
    }

    fun saveConfig() {
        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        // MutableMap<String, MutableMap<ConfigKey, ConfigValue<*>>> to
        // MutableMap<String, MutableMap<String, *>>
        val stringKeyConfig = config.mapValues { (_, entries) ->
            entries.mapKeys { (key, _) ->
                key.name
            }.mapValues { (_, value) ->
                value.value
            }
        }

        val jsonStr = gson.toJson(stringKeyConfig)
        configFile.writeText(jsonStr)
    }

    fun loadConfig() {
        if (configFile.exists()) {
            val jsonStr = configFile.readText()
            val type = object : TypeToken<MutableMap<String, MutableMap<String, *>>>() {}.type

            // Convert JSON to MutableMap
            val stringKeyConfig: MutableMap<String, MutableMap<String, *>> =
                gson.fromJson(jsonStr, type) ?: run {
                    configFile.delete()
                    mutableMapOf()
                }

            config.forEach { (section, entries) ->
                entries.forEach entriesForEach@ { (key, value) ->
                    val stringKey = key.name
                    val loadedValue = stringKeyConfig[section]?.get(stringKey) ?: return@entriesForEach
                    value.set(loadedValue)
                }
            }
        }
    }
}