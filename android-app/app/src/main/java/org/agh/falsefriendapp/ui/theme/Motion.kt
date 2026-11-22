package org.agh.falsefriendapp.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.TransformOrigin
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

const val FLAME_WOBBLE_DEGREES = 6f
const val FLAME_WOBBLE_MS = 450
const val FLAME_SWELL_SCALE = 1.15f
const val FLAME_SWELL_MS = 700
val FLAME_PIVOT = TransformOrigin(0.5f, 1f)

const val BELL_SWING_DEGREES = 15f
const val BELL_SWING_BACK_DEGREES = -10f
const val BELL_SETTLE_MS = 300
const val BELL_JIGGLE_DEGREES = 4f
const val BELL_SWING_MS = 600
const val BELL_JIGGLE_STEP_MS = 60
const val BELL_RETURN_MS = 450
val BELL_PIVOT = TransformOrigin(0.5f, 0f)