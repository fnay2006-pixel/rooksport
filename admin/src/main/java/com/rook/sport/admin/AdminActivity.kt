package com.rook.sport.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.rook.sport.admin.ui.AdminTheme
import com.rook.sport.admin.ui.Bg
import com.rook.sport.admin.ui.DashboardScreen
import com.rook.sport.admin.ui.LoginScreen

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AdminTheme {
                Surface(Modifier.fillMaxSize(), color = Bg) {
                    var loggedIn by rememberSaveable { mutableStateOf(false) }
                    if (loggedIn) {
                        DashboardScreen(onLogout = { loggedIn = false })
                    } else {
                        LoginScreen(onSuccess = { loggedIn = true })
                    }
                }
            }
        }
    }
}
