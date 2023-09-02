package me.glxphs.gmod.config.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterConfig(
    val section: String
)
