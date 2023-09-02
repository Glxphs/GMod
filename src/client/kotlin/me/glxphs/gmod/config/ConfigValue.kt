package me.glxphs.gmod.config

data class ConfigValue<T>(
    var value : T
) {
    fun get() : T {
        return value
    }

    fun set(value: Any) {
        this.value = value as? T ?: return
    }
}
