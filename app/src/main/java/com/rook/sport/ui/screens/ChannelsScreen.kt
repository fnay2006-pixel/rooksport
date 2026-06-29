package com.rook.sport.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.rook.sport.ui.components.CategoryChip
import com.rook.sport.ui.components.LiveBadge
import com.rook.sport.ui.theme.*

@Composable
fun ChannelsScreen(channels: List<Channel>, onChannel: (String) -> Unit) {
    var selected by remember { mutableStateOf("الكل") }
    val cats = listOf("الكل", "رياضة", "أخبار", "أفلام", "وثائقي")
    val filtered = remember(selected, channels) {
        if (selected == "الكل") channels
        else channels.filter { it.category == selected }
    }
    LazyColumn(
        Modifier.fillMaxSize().background(ZonaBg),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text("القنوات", color = ZonaText, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("${channels.size} قناة", color = ZonaGold,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        item {
            Row(
                Modifier.padding(16.dp, 0.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(ZonaCard).border(1.dp, ZonaLine, RoundedCornerShape(14.dp))
                    .padding(14.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null, tint = ZonaMuted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text("ابحث عن قناة...", color = ZonaMuted, fontSize = 13.sp)
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(16.dp, 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cats) { c -> CategoryChip(c, "", selected == c) { selected = c } }
            }
        }
        items(filtered) { ch -> ChannelListRow(ch) { onChannel(ch.id) } }
    }
}

@Composable
private fun ChannelListRow(ch: Channel, onClick: () -> Unit) {
    Row(
        Modifier.padding(16.dp, 5.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(ZonaCard).border(1.dp, ZonaLine, RoundedCornerShape(16.dp))
            .clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(52.dp).clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(ch.gradient.map { Color(it) })),
            contentAlignment = Alignment.Center
        ) { Text(ch.abbr, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(ch.name, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(ch.nowPlaying, color = ZonaMuted, fontSize = 11.sp)
        }
        if (ch.isLive) LiveBadge("LIVE")
        else Box(
            Modifier.border(1.dp, ZonaGold, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) { Text(ch.quality, color = ZonaGold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold) }
    }
}
