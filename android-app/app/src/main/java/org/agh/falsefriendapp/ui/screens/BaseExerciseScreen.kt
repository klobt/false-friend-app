package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.agh.falsefriendapp.data.model.TranslationExercise
import org.agh.falsefriendapp.ui.components.AppButton

@Composable
fun BaseExerciseScreen(
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    exercise: TranslationExercise,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.LightGray)
                .padding(15.dp)
        ) {
            Text("Pytanie $currentStep z $totalSteps")
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(instruction)
        Spacer(modifier = Modifier.height(10.dp))
//        content()
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = exercise.sentence,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        exercise.options.forEachIndexed { index, option ->
            AppButton(text = option, onClick = { onAnswerSelected(index) })
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
