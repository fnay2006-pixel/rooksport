package com.rook.sport.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rook.sport.admin.data.AdminAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onSuccess: () -> Unit) {
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(Bg).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(86.dp).clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(Gold, Gold2))),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Lock, null, tint = Bg, modifier = Modifier.size(42.dp)) }

        Spacer(Modifier.height(22.dp))
        Row {
            Text("ROOK", color = TextC, fontWeight = FontWeight.Black, fontSize = 26.sp)
            Text(" Admin", color = Gold, fontWeight = FontWeight.Black, fontSize = 26.sp)
        }
        Text("لوحة تحكم المباريات والبث", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it; error = false },
            label = { Text("كلمة المرور") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = error,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold, unfocusedBorderColor = Line,
                focusedLabelColor = Gold, cursorColor = Gold,
                focusedTextColor = TextC, unfocusedTextColor = TextC,
                errorBorderColor = Red
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error) {
            Spacer(Modifier.height(8.dp))
            Text("كلمة المرور غير صحيحة", color = Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(Gold, Gold2)))
                .clickable {
                    if (pass == AdminAuth.PASSWORD) onSuccess() else error = true
                }.padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("دخول", color = Bg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("🔒 هذا التطبيق خاص بالمشرف فقط", color = Muted, fontSize = 11.sp)
    }
}
