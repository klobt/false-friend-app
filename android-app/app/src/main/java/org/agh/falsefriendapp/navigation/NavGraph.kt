package org.agh.falsefriendapp.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.ui.screens.DefinitionExerciseScreen
import org.agh.falsefriendapp.ui.screens.FriendsScreen
import org.agh.falsefriendapp.ui.screens.LoginScreen
import org.agh.falsefriendapp.ui.screens.MatchExerciseScreen
import org.agh.falsefriendapp.ui.screens.NotificationsScreen
import org.agh.falsefriendapp.ui.screens.ReviewScreen
import org.agh.falsefriendapp.ui.screens.SettingsScreen
import org.agh.falsefriendapp.ui.screens.StatsScreen
import org.agh.falsefriendapp.ui.screens.SummaryScreen
import org.agh.falsefriendapp.ui.screens.TranslationExerciseScreen
import org.agh.falsefriendapp.ui.screens.UserMainScreen
import org.agh.falsefriendapp.viewmodel.SessionReviewViewModel
import org.agh.falsefriendapp.viewmodel.SessionViewModel
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import org.agh.falsefriendapp.ui.theme.*

@Composable
private fun AnimatedContentScope.Tilt(content: @Composable () -> Unit) {
    val angle by transition.animateFloat(
        transitionSpec = { spring(TILT_DAMPING, TILT_STIFFNESS) },
        label = "tilt"
    ) { state ->
        if (state == EnterExitState.Visible) 0f else TILT_ANGLE_DEGREES
    }
    Box(
        Modifier.graphicsLayer {
            rotationZ = angle
            transformOrigin = TransformOrigin(TILT_PIVOT_X, TILT_PIVOT_Y)
        }
    ) { content() }
}

private fun NavGraphBuilder.myComposable(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route) { entry ->
        Tilt { content(entry) }
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val sessionReviewViewModel: SessionReviewViewModel = viewModel()
    val sessionViewModel: SessionViewModel = hiltViewModel()

    val fadeInSpec = fadeIn(tween(TRANSITION_DURATION_MS))
    val fadeOutSpec = fadeOut(tween(TRANSITION_DURATION_MS))
    val dirLeft = AnimatedContentTransitionScope.SlideDirection.Left
    val dirRight = AnimatedContentTransitionScope.SlideDirection.Right

    val navigateToLogin: () -> Unit = {
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.USER_HOME) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.forcedLogout.collect {
            navigateToLogin()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = {
            slideIntoContainer(dirLeft, tween(TRANSITION_DURATION_MS)) + fadeInSpec
        },
        exitTransition = {
            slideOutOfContainer(dirLeft, tween(TRANSITION_DURATION_MS)) + fadeOutSpec
        },
        popEnterTransition = {
            slideIntoContainer(dirRight, tween(TRANSITION_DURATION_MS)) + fadeInSpec
        },
        popExitTransition = {
            slideOutOfContainer(dirRight, tween(TRANSITION_DURATION_MS)) + fadeOutSpec
        }
    ) {
        myComposable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Routes.USER_HOME) {
                        popUpTo(Routes.LOGIN) {inclusive = true}
                        launchSingleTop = true
                    }
                }
            )
        }

        myComposable(Routes.USER_HOME) {
            UserMainScreen(
                onStartTranslation = {
                    navController.navigate(Routes.TRANSLATION) {
                        launchSingleTop = true
                    }
                },
                onStartDefinition = {
                    navController.navigate(Routes.DEFINITION) {
                        launchSingleTop = true
                    }
                },
                onStartMatch = {
                    navController.navigate(Routes.MATCH) {
                        launchSingleTop = true
                    }
                },
                onStartSettings = {
                    navController.navigate(Routes.SETTINGS) {
                        launchSingleTop = true
                    }
                },
                onStartFriends = {
                    navController.navigate(Routes.FRIENDS) {
                        launchSingleTop = true
                    }
                },
                onStartNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS) {
                        launchSingleTop = true
                    }
                },
                onStartStats = {
                    navController.navigate(Routes.STATS) {
                        launchSingleTop = true
                    }
                }
            )
        }

        myComposable(Routes.TRANSLATION) {
            TranslationExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.TRANSLATION)) {
                        popUpTo(Routes.TRANSLATION) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        myComposable(Routes.DEFINITION) {
            DefinitionExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.DEFINITION)) {
                        popUpTo(Routes.DEFINITION) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        myComposable(Routes.MATCH) {
            MatchExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.MATCH)) {
                        popUpTo(Routes.MATCH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        myComposable(Routes.SUMMARY) { backStackEntry ->
            val score = backStackEntry.arguments
                ?.getString("score")
                ?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments
                ?.getString("totalQuestions")
                ?.toIntOrNull() ?: 0
            val exerciseType = backStackEntry.arguments
                ?.getString("exerciseType")
                ?.let { ExerciseType.fromValue(it) }
            SummaryScreen(
                score = score,
                totalQuestions = totalQuestions,
                exerciseType = exerciseType,
                onShowAnswers = {
                    navController.navigate(Routes.REVIEW) { launchSingleTop = true }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        myComposable(Routes.REVIEW) {
            val items by sessionReviewViewModel.items.collectAsState()
            ReviewScreen(items = items, onNavigateBack = { navController.popBackStack() })
        }

        myComposable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateHome = { navController.popBackStack() },
                onLoggedOut = navigateToLogin
            )
        }

        myComposable(Routes.FRIENDS) {
            FriendsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        myComposable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        myComposable(Routes.STATS) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
