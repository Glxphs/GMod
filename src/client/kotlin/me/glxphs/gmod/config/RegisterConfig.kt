package me.glxphs.gmod.config

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterConfig(
    val section: String
)
