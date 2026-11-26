package org.agh.falsefriendapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BaseButton(
    text: String,
    height: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .width(300.dp)
            .height(height),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
