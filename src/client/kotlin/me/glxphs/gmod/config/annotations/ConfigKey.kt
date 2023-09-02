package me.glxphs.gmod.config.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigKey(
    val name: String = "",
    val description: String = "",
    val hidden: Boolean = false,
    val order: Int = 0
)
