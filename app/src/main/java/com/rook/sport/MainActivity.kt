package com.rook.sport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rook.sport.navigation.AppNav
import com.rook.sport.ui.theme.ZonaBg
import com.rook.sport.ui.theme.ZonaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ZonaTheme {
                Surface(Modifier.fillMaxSize(), color = ZonaBg) {
                    AppNav()
                }
            }
        }
    }
}
