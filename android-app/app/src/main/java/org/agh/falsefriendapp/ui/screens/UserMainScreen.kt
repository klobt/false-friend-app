package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.agh.falsefriendapp.ui.components.AppButton

@Composable
fun UserMainScreen(
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray)
                .padding(20.dp)
        ) {
            Text(
                text = "Witaj z powrotem!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Mateusz",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Ćwiczenia",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(text = "Wybierz rodzaj ćwiczenia i zacznij naukę")

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                AppButton(text = "Wybierz tłumaczenie", onClick = onStartTranslation)
                Spacer(modifier = Modifier.height(15.dp))
                AppButton(text = "Wybierz definicję", onClick = onStartDefinition)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        UserMainScreen(onStartTranslation = {}, onStartDefinition = {})
    }
}
