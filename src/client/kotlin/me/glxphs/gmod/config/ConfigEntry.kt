package me.glxphs.gmod.config

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigEntry(
    val name: String = "",
    val description: String = ""
)
