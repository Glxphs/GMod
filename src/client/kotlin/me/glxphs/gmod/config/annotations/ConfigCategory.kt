package me.glxphs.gmod.config.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigCategory(
    val name: String,
    val description: String = "",
    val hidden: Boolean = false,
    val order: Int = Int.MAX_VALUE,
)
