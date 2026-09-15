package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.state.MatchPairResult
import org.agh.falsefriendapp.ui.state.ReviewItem
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.ui.theme.success
import org.agh.falsefriendapp.ui.theme.warning

@Composable
fun ReviewScreen(
    items: List<ReviewItem>,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {
        ScreenHeader("Odpowiedzi", onNavigateBack)
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Brak odpowiedzi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    when (item) {
                        is ReviewItem.Choice -> ChoiceReviewCard(index + 1, item)
                        is ReviewItem.Match -> MatchReviewCard(index + 1, item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceReviewCard(number: Int, item: ReviewItem.Choice) {
    val selectedColor = if (item.correct) {
        MaterialTheme.colorScheme.success
    } else {
        MaterialTheme.colorScheme.error
    }

    ReviewCard {
        ReviewCardHeader(title = "Pytanie $number") {
            StatusIcon(correct = item.correct)
        }
        Text(
            text = item.question,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        AnswerLine(
            label = "Twoja odpowiedź",
            answer = item.options.getOrNull(item.selectedAnswerIndex).orEmpty(),
            color = selectedColor
        )

        if (!item.correct) {
            AnswerLine(
                label = "Poprawna odpowiedź",
                answer = item.options.getOrNull(item.correctAnswerIndex).orEmpty(),
                color = MaterialTheme.colorScheme.success
            )
        }
    }
}

@Composable
private fun MatchReviewCard(number: Int, item: ReviewItem.Match) {
    val pairsSize = item.pairs.size
    val counterColor = when (item.correctCount) {
        pairsSize -> MaterialTheme.colorScheme.success
        0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.warning
    }

    ReviewCard {
        ReviewCardHeader(title = "Łączenie $number") {
            Text(
                text = "${item.correctCount} / $pairsSize",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = counterColor
            )
        }

        item.pairs.forEachIndexed { index, pair ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
            MatchPairRow(pair)
        }
    }
}

@Composable
private fun MatchPairRow(pair: MatchPairResult) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pair.leftText,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(16.dp)
            )

            val textColor = if (pair.correct) {
                MaterialTheme.colorScheme.success
            } else {
                MaterialTheme.colorScheme.error
            }
            Text(
                text = pair.selectedText,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusIcon(correct = pair.correct, modifier = Modifier.size(20.dp))
        }

        if (!pair.correct) {
            Text(
                text = "Poprawnie: ${pair.correctText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.success,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ReviewCardHeader(title: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun ReviewCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun StatusIcon(correct: Boolean, modifier: Modifier = Modifier) {
    if (correct) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Poprawnie",
            tint = MaterialTheme.colorScheme.success,
            modifier = modifier
        )
    }
    else {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = "Błędnie",
            tint = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
    }
}

@Composable
private fun AnswerLine(label: String, answer: String, color: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    FalseFriendAppTheme {
        ReviewScreen(
            items = listOf(
                ReviewItem.Choice(
                    question = "eventually",
                    options = listOf("ewentualnie", "w końcu", "wydarzenie"),
                    correctAnswerIndex = 1,
                    selectedAnswerIndex = 0
                ),
                ReviewItem.Choice(
                    question = "fabric",
                    options = listOf("fabryka", "tkanina", "fabuła"),
                    correctAnswerIndex = 1,
                    selectedAnswerIndex = 1
                ),
                ReviewItem.Match(
                    pairs = listOf(
                        MatchPairResult("actual", "aktualny", "rzeczywisty", false),
                        MatchPairResult("lunatic", "szaleniec", "szaleniec", true),
                        MatchPairResult("sympathetic", "sympatyczny", "współczujący", false)
                    )
                )
            ),
            onNavigateBack = {}
        )
    }
}
