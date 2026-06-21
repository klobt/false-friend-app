package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.agh.falsefriendapp.ui.components.AppButton

@Composable
fun SummaryScreen(score: Int, onNavigateHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Ćwiczenie ukończone!",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Wybierz tłumaczenie",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(80.dp))
        Text("Poprawnych odpowiedzi: $score")
        Spacer(modifier = Modifier.height(120.dp))
        AppButton(text = "Menu główne", onClick = onNavigateHome)
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    MaterialTheme {
        SummaryScreen(10, onNavigateHome = {})
    }
}
