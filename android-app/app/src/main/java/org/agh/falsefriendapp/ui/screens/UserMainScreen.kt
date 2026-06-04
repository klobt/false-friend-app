package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UserMainScreen(onStartLearning: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            text = "Witaj z powrotem!",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Mateusz",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(80.dp))
        Text(text = "Ćwiczenia")
        Text(text = "Wybierz rodzaj ćwiczenia i zacznij naukę")
        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onStartLearning,
                modifier = Modifier
                    .width(300.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Text("Wybierz tłumaczenie")
            }
        }
    }
}
