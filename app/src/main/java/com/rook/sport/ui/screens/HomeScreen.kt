package com.rook.sport.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import com.rook.sport.data.model.Channel
import com.rook.sport.data.model.Match
import com.rook.sport.data.repo.DemoRepository
import com.rook.sport.ui.components.*
import com.rook.sport.ui.theme.*

@Composable
fun HomeScreen(
    matches: List<Match>,
    channels: List<Channel>,
    notice: String?,
    onWatch: (String) -> Unit,
    onMatch: (String) -> Unit,
    onChannel: (String) -> Unit
) {
    var selectedCat by remember { mutableStateOf("all") }
    val featured = matches.firstOrNull { it.isLive } ?: matches.firstOrNull()
    val rest = matches.filter { it.id != featured?.id }
    LazyColumn(
        Modifier.fillMaxSize().background(ZonaBg),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item { TopBar() }
        if (notice != null) item { NoticeBar(notice) }
        if (featured != null) item {
            FeaturedMatchCard(
                match = featured,
                onWatch = { onWatch(featured.id) },
                onOpen = { onMatch(featured.id) }
            )
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(DemoRepository.categories) { cat ->
                    CategoryChip(cat.label, cat.emoji, selectedCat == cat.id) {
                        selectedCat = cat.id
                    }
                }
            }
        }
        if (channels.isNotEmpty()) {
            item { SectionHeader("القنوات المباشرة", "عرض الكل") }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(channels.take(6)) { ch ->
                        ChannelCard(ch) { onChannel(ch.id) }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SectionHeader("المباريات", "الجدول") }
        items(rest) { m ->
            MatchRow(m) { onMatch(m.id) }
        }
    }
}

@Composable
private fun NoticeBar(text: String) {
    Row(
        Modifier.padding(16.dp, 4.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(ZonaGold.copy(alpha = 0.12f))
            .border(1.dp, ZonaGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("📢", fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = ZonaGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TopBar() {
    Row(
        Modifier.fillMaxWidth().padding(18.dp, 14.dp, 18.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(goldBrush()),
                contentAlignment = Alignment.Center
            ) { Text("R", fontWeight = FontWeight.Black, color = ZonaBg, fontSize = 20.sp) }
            Spacer(Modifier.width(10.dp))
            Column {
                Row {
                    Text("ROOK", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = ZonaText)
                    Text(" Sport", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = ZonaGold)
                }
                Text("LIVE SPORTS HUB", fontSize = 9.sp, color = ZonaMuted, letterSpacing = 3.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconBox(Icons.Default.Search)
            IconBox(Icons.Default.Notifications, badge = true)
        }
    }
}

@Composable
private fun IconBox(icon: androidx.compose.ui.graphics.vector.ImageVector, badge: Boolean = false) {
    Box(
        Modifier.size(38.dp).clip(RoundedCornerShape(11.dp))
            .background(ZonaCard).border(1.dp, ZonaLine, RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = ZonaText, modifier = Modifier.size(19.dp))
        if (badge) Box(
            Modifier.align(Alignment.TopStart).padding(7.dp).size(8.dp)
                .clip(CircleShape).background(ZonaRed)
        )
    }
}

@Composable
fun FeaturedMatchCard(match: Match, onWatch: () -> Unit, onOpen: () -> Unit) {
    Column(
        Modifier.padding(16.dp, 4.dp).fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1B2A52), Color(0xFF0D1426))))
            .border(1.dp, ZonaLine, RoundedCornerShape(24.dp))
            .clickable { onOpen() }
            .padding(18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            LiveBadge()
            Text("⚽ ${match.league}", color = ZonaMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            TeamCol(match.homeAbbr, match.homeColor, match.homeName)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${match.homeScore} - ${match.awayScore}",
                    fontWeight = FontWeight.Black, fontSize = 38.sp, color = ZonaText)
                Text("⏱ ${match.minute}", color = ZonaAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            TeamCol(match.awayAbbr, match.awayColor, match.awayName)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(goldBrush()).clickable { onWatch() }.padding(vertical = 13.dp),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = ZonaBg, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("مشاهدة البث المباشر — HD", color = ZonaBg, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun TeamCol(abbr: String, color: Long, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Crest(abbr, color, 54)
        Spacer(Modifier.height(8.dp))
        Text(name, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun ChannelCard(ch: Channel, onClick: () -> Unit) {
    Column(
        Modifier.width(160.dp).clip(RoundedCornerShape(18.dp)).background(ZonaCard)
            .border(1.dp, ZonaLine, RoundedCornerShape(18.dp))
            .clickable { onClick() }.padding(14.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(ch.gradient.map { Color(it) })),
            contentAlignment = Alignment.Center
        ) {
            Text(ch.abbr, fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp)
            Box(Modifier.align(Alignment.TopStart).padding(6.dp)
                .clip(RoundedCornerShape(6.dp)).background(Color.Black.copy(0.5f))
                .padding(horizontal = 7.dp, vertical = 2.dp)) {
                Text(ch.quality, color = ZonaGold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            }
            if (ch.isLive) Box(Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                LiveBadge("LIVE")
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(ch.name, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(ch.nowPlaying, color = ZonaMuted, fontSize = 11.sp)
    }
}

@Composable
fun MatchRow(m: Match, onClick: () -> Unit) {
    Row(
        Modifier.padding(16.dp, 5.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(ZonaCard).border(1.dp, ZonaLine, RoundedCornerShape(16.dp))
            .clickable { onClick() }.padding(14.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            TeamLine(m.homeAbbr, m.homeColor, m.homeName)
            Spacer(Modifier.height(6.dp))
            TeamLine(m.awayAbbr, m.awayColor, m.awayName)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(54.dp)) {
            Text(m.kickoff ?: "", color = ZonaGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(m.dayLabel ?: "", color = ZonaMuted, fontSize = 9.sp)
        }
        Box(Modifier.width(60.dp), contentAlignment = Alignment.Center) {
            Text(m.leagueShort, color = ZonaMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TeamLine(abbr: String, color: Long, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Crest(abbr, color, 22)
        Spacer(Modifier.width(8.dp))
        Text(name, color = ZonaText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
