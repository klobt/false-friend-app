package org.agh.falsefriendapp.ui.theme

import androidx.compose.animation.core.Spring
import kotlin.time.Duration.Companion.milliseconds

const val TRANSITION_DURATION_MS = 300
const val TILT_ANGLE_DEGREES = 8f
const val TILT_PIVOT_X = 0.5f
const val TILT_PIVOT_Y = 1f
const val TILT_DAMPING = Spring.DampingRatioMediumBouncy
const val TILT_STIFFNESS = Spring.StiffnessLow

val ANSWER_FEEDBACK_DELAY = 400.milliseconds
const val OVERLAY_ALPHA = 0.35f
const val OVERLAY_FADE_MS = 150

val BUTTON_TILT_ANGLES = listOf(-6f, 6f, -4f, 4f, 0f)
const val BUTTON_SWELL_SCALE = 1.2f