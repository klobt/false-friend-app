package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.agh.falsefriendapp.ui.state.ReviewItem
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme

@Composable
fun ReviewScreen(
    items: List<ReviewItem>,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        ScreenHeader(onNavigateBack)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    FalseFriendAppTheme {
        ReviewScreen()
    }
}
