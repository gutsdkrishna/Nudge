package com.kalki.nudge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kalki.nudge.navigation.NudgeNavigation
import com.kalki.nudge.ui.theme.NudgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NudgeTheme {
                NudgeNavigation()
            }
        }
    }
}