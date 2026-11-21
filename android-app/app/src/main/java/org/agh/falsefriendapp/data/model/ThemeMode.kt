package org.agh.falsefriendapp.data.model

enum class ThemeMode(val key: String) {
    LIGHT("light"),
    DARK("dark");

    companion object {
        val DEFAULT = LIGHT

        fun fromKey(key: String?): ThemeMode {
            return entries.find { it.key == key } ?: DEFAULT
        }
    }
}
