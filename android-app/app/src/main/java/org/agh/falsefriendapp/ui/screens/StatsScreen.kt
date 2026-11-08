package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import org.agh.falsefriendapp.data.model.DailyAccuracy
import org.agh.falsefriendapp.ui.components.NO_VALUE
import org.agh.falsefriendapp.ui.components.ScreenHeader
import org.agh.falsefriendapp.ui.components.SectionCard
import org.agh.falsefriendapp.ui.components.answersLabel
import org.agh.falsefriendapp.ui.state.STATS_DAYS
import org.agh.falsefriendapp.ui.state.StatsUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private val DAY_FORMAT = DateTimeFormatter.ofPattern("d.MM")
private const val LABEL_SPACING = 7
private const val PERCENT_STEP = 25.0
private const val MAX_PERCENT = 100.0

@Composable
fun StatsScreen(onNavigateBack: () -> Unit) {
    StatsContent(StatsUiState(), onNavigateBack)
}

@Composable
private fun StatsContent(
    state: StatsUiState,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        ScreenHeader("Statystyki", onNavigateBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionCard(title = "SKUTECZNOŚĆ · OSTATNIE $STATS_DAYS DNI") {
                if (state.days.isEmpty()) {
                    EmptyChart()
                } else {
                    AccuracyChart(state.days, state.firstDay())
                }
            }
            if (state.days.isNotEmpty()) {
                SummarySection(state)
            }
        }
    }
}

@Composable
private fun AccuracyChart(days: List<DailyAccuracy>, firstDay: LocalDate) {
    val daysByIndex = remember(days, firstDay) {
        days.sortedBy { it.date }.associateBy { dayIndex(firstDay, it.date) }
    }
    val model = remember(daysByIndex) {
        CartesianChartModel(
            LineCartesianLayerModel.build {
                series(
                    x = daysByIndex.keys.toList(),
                    y = daysByIndex.values.map { it.percent() }
                )
            }
        )
    }

    val primary = MaterialTheme.colorScheme.primary
    val axisLabel = rememberTextComponent(
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(primary)),
        pointProvider = LineCartesianLayer.PointProvider.single(
            LineCartesianLayer.Point(
                rememberShapeComponent(fill = Fill(primary), shape = CircleShape),
                8.dp
            )
        )
    )
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.inverseOnSurface
            ),
            padding = Insets(8.dp, 4.dp),
            background = rememberShapeComponent(
                fill = Fill(MaterialTheme.colorScheme.inverseSurface),
                shape = RoundedCornerShape(8.dp)
            )
        ),
        valueFormatter = remember(daysByIndex) {
            DefaultCartesianMarker.ValueFormatter { _, targets ->
                daysByIndex[targets.first().x.roundToInt()]?.let { markerText(it) } ?: ""
            }
        }
    )
    val bottomFormatter = remember(firstDay) {
        CartesianValueFormatter { _, x, _ ->
            firstDay.plusDays(x.roundToInt().toLong()).format(DAY_FORMAT)
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(line),
                    rangeProvider = CartesianLayerRangeProvider.fixed(
                        minX = 0.0,
                        maxX = STATS_DAYS - 1.0,
                        minY = 0.0,
                        maxY = MAX_PERCENT
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = axisLabel,
                    valueFormatter = PercentFormatter,
                    itemPlacer = remember { VerticalAxis.ItemPlacer.step({ PERCENT_STEP }) }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = axisLabel,
                    valueFormatter = bottomFormatter,
                    guideline = null,
                    itemPlacer = remember {
                        HorizontalAxis.ItemPlacer.aligned(
                            spacing = { LABEL_SPACING },
                            offset = { (STATS_DAYS - 1) % LABEL_SPACING }
                        )
                    }
                ),
                marker = marker,
                markerController = CartesianMarkerController.rememberToggleOnTap(),
                getXStep = { _, _, _ -> 1.0 }
            ),
            model = model,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .semantics {
                    contentDescription = "Wykres skuteczności z ostatnich $STATS_DAYS dni"
                },
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content)
        )
    }
}

@Composable
private fun EmptyChart() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ShowChart,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Zrób kilka ćwiczeń, żeby zobaczyć postęp",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SummarySection(state: StatsUiState) {
    SectionCard(title = "PODSUMOWANIE") {
        SummaryRow("Średnia skuteczność", state.accuracyPercent()?.let { "$it%" } ?: NO_VALUE)
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        SummaryRow("Dni z ćwiczeniami", "${state.days.size} z $STATS_DAYS")
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        SummaryRow("Sesje", state.totalSessions().toString())
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        SummaryRow("Odpowiedzi", state.totalAnswers().toString())
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private val PercentFormatter = CartesianValueFormatter { _, value, _ ->
    "${value.roundToInt()}%"
}

private fun dayIndex(firstDay: LocalDate, date: LocalDate): Int {
    return ChronoUnit.DAYS.between(firstDay, date).toInt()
}

private fun markerText(day: DailyAccuracy): String {
    return "${day.date.format(DAY_FORMAT)} · ${day.percent().roundToInt()}% · ${answersLabel(day.answers)}"
}

private val PREVIEW_TODAY = LocalDate.of(2026, 11, 7)

private fun previewDays(): List<DailyAccuracy> {
    val results = listOf(
        29 to (12 to 20), 27 to (15 to 20), 26 to (9 to 16), 24 to (18 to 22),
        20 to (14 to 20), 19 to (17 to 20), 15 to (8 to 10), 12 to (21 to 25),
        11 to (19 to 24), 7 to (22 to 25), 4 to (27 to 30), 2 to (18 to 20),
        0 to (24 to 26)
    )
    return results.map { (daysAgo, score) ->
        DailyAccuracy(
            date = PREVIEW_TODAY.minusDays(daysAgo.toLong()),
            sessions = if (score.second > 20) 2 else 1,
            correct = score.first,
            answers = score.second
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsContentPreview() {
    FalseFriendAppTheme {
        StatsContent(
            state = StatsUiState(today = PREVIEW_TODAY, days = previewDays()),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsContentEmptyPreview() {
    FalseFriendAppTheme {
        StatsContent(
            state = StatsUiState(today = PREVIEW_TODAY),
            onNavigateBack = {}
        )
    }
}
