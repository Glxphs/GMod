package me.glxphs.gmod.utils

import java.util.*

class ClickCounter {
    private val clicks: Queue<Long> = LinkedList()

    /**
     * Register a new click.
     */
    fun onClick() {
        clicks.add(System.currentTimeMillis() + 1000L)
    }

    val cps: Int
        /**
         * Get the amount of clicks registered in the past second.
         *
         * @return The clicks per second
         */
        get() {
            val time = System.currentTimeMillis()
            while (!clicks.isEmpty() && clicks.peek() < time) {
                clicks.remove()
            }
            return clicks.size
        }
}