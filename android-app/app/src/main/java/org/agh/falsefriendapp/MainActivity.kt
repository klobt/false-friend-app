package org.agh.falsefriendapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.agh.falsefriendapp.data.model.ThemeMode
import org.agh.falsefriendapp.navigation.NavGraph
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.ThemeViewModel

private val LightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val DarkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()

            themeMode?.let { mode ->
                val darkTheme = mode == ThemeMode.DARK

                DisposableEffect(darkTheme) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT
                        ) { darkTheme },
                        navigationBarStyle = SystemBarStyle.auto(
                            LightScrim,
                            DarkScrim
                        ) { darkTheme }
                    )
                    onDispose {}
                }

                FalseFriendAppTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph()
                    }
                }
            }
        }
    }
}
