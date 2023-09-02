package me.glxphs.gmod.config

data class Config<T>(
    var value : T,
    val hidden: Boolean = false,
    val order: Int = 0,
) {
    fun get() : T {
        return value
    }

    fun set(value: Any) {
        this.value = value as? T ?: return
    }
}
