package com.rook.sport.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rook.sport.data.model.Match
import com.rook.sport.data.repo.DemoRepository
import com.rook.sport.ui.components.Crest
import com.rook.sport.ui.theme.*

@Composable
fun MatchDetailScreen(match: Match?, onBack: () -> Unit, onWatch: (String) -> Unit) {
    val m = match ?: DemoRepository.featuredMatch
    val tabs = listOf("الأحداث", "التشكيلة", "الإحصائيات", "H2H")
    var tab by remember { mutableStateOf("الأحداث") }

    LazyColumn(Modifier.fillMaxSize().background(ZonaBg)) {
        item {
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF1B2A52), Color(0xFF0D1426))))
                    .padding(16.dp, 20.dp, 16.dp, 16.dp)
            ) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(34.dp).clip(CircleShape)
                        .background(Color.White.copy(0.1f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚽ ${m.league}", color = ZonaMuted, fontSize = 11.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
                            Crest(m.homeAbbr, m.homeColor, 58)
                            Spacer(Modifier.height(8.dp))
                            Text(m.homeName, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val score = if (m.homeScore != null) "${m.homeScore} - ${m.awayScore}" else "VS"
                            Text(score, color = ZonaText, fontWeight = FontWeight.Black, fontSize = 40.sp)
                            val status = if (m.isLive) "⏱ ${m.minute} مباشر" else "${m.kickoff} ${m.dayLabel}"
                            Box(Modifier.clip(RoundedCornerShape(12.dp))
                                .background(ZonaAccent.copy(0.12f)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                                Text(status, color = ZonaAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
                            Crest(m.awayAbbr, m.awayColor, 58)
                            Spacer(Modifier.height(8.dp))
                            Text(m.awayName, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(16.dp, 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(tabs) { t ->
                    val on = t == tab
                    Box(
                        Modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (on) ZonaGold else ZonaCard)
                            .clickable { tab = t }.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(t, color = if (on) ZonaBg else ZonaMuted,
                            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
        items(DemoRepository.featuredEvents) { e ->
            Row(
                Modifier.fillMaxWidth().padding(16.dp, 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(e.minute, color = ZonaGold, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
                    modifier = Modifier.width(34.dp))
                Text(e.icon, fontSize = 16.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(e.title, color = ZonaText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(e.subtitle, color = ZonaMuted, fontSize = 10.sp)
                }
            }
        }
        item {
            Row(
                Modifier.padding(16.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(ZonaGold, ZonaGold2)))
                    .clickable { onWatch(m.id) }.padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = ZonaBg, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("مشاهدة المباراة مباشرة", color = ZonaBg, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
    }
}
