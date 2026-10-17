package org.agh.falsefriendapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import org.agh.falsefriendapp.data.model.Avatar

fun Avatar.icon(): ImageVector {
    return when (this) {
        Avatar.PERSON -> Icons.Default.Person
        Avatar.FACE -> Icons.Default.Face
        Avatar.PETS -> Icons.Default.Pets
        Avatar.STAR -> Icons.Default.Star
        Avatar.SCHOOL -> Icons.Default.School
        Avatar.MUSIC -> Icons.Default.MusicNote
        Avatar.GAMES -> Icons.Default.SportsEsports
        Avatar.ROCKET -> Icons.Default.RocketLaunch
    }
}
