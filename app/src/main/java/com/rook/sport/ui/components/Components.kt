package com.rook.sport.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rook.sport.ui.theme.*

/** Gradient circular team crest with abbreviation. */
@Composable
fun Crest(abbr: String, color: Long, size: Int = 54) {
    val c = Color(color)
    Box(
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(c, c.copy(alpha = 0.55f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(abbr, fontWeight = FontWeight.Black, fontSize = (size / 2.6).sp, color = Color.White)
    }
}

/** Pulsing LIVE badge. */
@Composable
fun LiveBadge(text: String = "مباشر الآن") {
    val transition = rememberInfiniteTransition(label = "live")
    val alpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "alpha"
    )
    Row(
        Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(ZonaRed)
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = alpha))
        )
        Spacer(Modifier.width(6.dp))
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Pill chip used for category selection. */
@Composable
fun CategoryChip(label: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Brush.linearGradient(listOf(ZonaGold, ZonaGold2))
    else Brush.linearGradient(listOf(ZonaCard, ZonaCard))
    Row(
        Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(bg)
            .border(
                BorderStroke(1.dp, if (selected) Color.Transparent else ZonaLine),
                RoundedCornerShape(30.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$emoji $label",
            color = if (selected) ZonaBg else ZonaMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}

/** Section header with optional action. */
@Composable
fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = ZonaText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        if (action != null) {
            Text(
                "$action ›",
                color = ZonaGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

/** Gold gradient brush helper. */
fun goldBrush() = Brush.linearGradient(listOf(ZonaGold, ZonaGold2))
