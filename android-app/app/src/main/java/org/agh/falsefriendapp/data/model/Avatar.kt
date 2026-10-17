package org.agh.falsefriendapp.data.model

enum class Avatar(val key: String) {
    PERSON("person"),
    FACE("face"),
    PETS("pets"),
    STAR("star"),
    SCHOOL("school"),
    MUSIC("music"),
    GAMES("games"),
    ROCKET("rocket");

    companion object {
        val DEFAULT = PERSON

        fun fromKey(key: String?): Avatar {
            return entries.find { it.key == key } ?: DEFAULT
        }
    }
}
