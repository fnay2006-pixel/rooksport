package com.rook.sport.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rook.sport.admin.data.*

@Composable
fun DashboardScreen(onLogout: () -> Unit) {
    val vm: AdminViewModel = viewModel()
    val matches by vm.matches.collectAsState()
    val channels by vm.channels.collectAsState()
    val loading by vm.loading.collectAsState()
    val message by vm.message.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    var tab by remember { mutableStateOf(0) }            // 0=مباريات 1=قنوات
    var editingMatch by remember { mutableStateOf<AdminMatch?>(null) }
    var editingChannel by remember { mutableStateOf<AdminChannel?>(null) }

    LaunchedEffect(Unit) { vm.loadAll() }
    LaunchedEffect(message) {
        message?.let { snackbar.showSnackbar(it); vm.clearMessage() }
    }

    // ===== شاشات التحرير =====
    if (editingMatch != null) {
        MatchEditor(editingMatch!!, onCancel = { editingMatch = null },
            onSave = { vm.saveMatch(it) { editingMatch = null } })
        return
    }
    if (editingChannel != null) {
        ChannelEditor(editingChannel!!, onCancel = { editingChannel = null },
            onSave = { vm.saveChannel(it) { editingChannel = null } })
        return
    }

    Scaffold(
        containerColor = Bg,
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (tab == 0) editingMatch = AdminMatch() else editingChannel = AdminChannel()
                },
                containerColor = Gold
            ) { Icon(Icons.Default.Add, null, tint = Bg) }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            // الهيدر
            Row(
                Modifier.fillMaxWidth().padding(18.dp, 14.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(11.dp))
                            .background(Brush.linearGradient(listOf(Gold, Gold2))),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Dashboard, null, tint = Bg, modifier = Modifier.size(20.dp)) }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("لوحة التحكم", color = TextC, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        Text("ROOK Admin", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row {
                    IconButton(onClick = { vm.loadAll() }) {
                        Icon(Icons.Default.Refresh, null, tint = Muted)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Red)
                    }
                }
            }

            // التبويبات
            Row(Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TabBtn("⚽ المباريات (${matches.size})", tab == 0) { tab = 0 }
                TabBtn("📺 القنوات (${channels.size})", tab == 1) { tab = 1 }
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (tab == 0) {
                if (matches.isEmpty()) EmptyState("لا توجد مباريات بعد.\nاضغط + لإضافة مباراة")
                LazyColumn(contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 90.dp)) {
                    items(matches) { m ->
                        MatchRow(m, onEdit = { editingMatch = m }, onDelete = { vm.deleteMatch(m.id) })
                    }
                }
            } else {
                if (channels.isEmpty()) EmptyState("لا توجد قنوات بعد.\nاضغط + لإضافة قناة")
                LazyColumn(contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 90.dp)) {
                    items(channels) { c ->
                        ChannelRow(c, onEdit = { editingChannel = c }, onDelete = { vm.deleteChannel(c.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TabBtn(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(12.dp))
            .background(if (on) Brush.linearGradient(listOf(Gold, Gold2)) else Brush.linearGradient(listOf(Card, Card)))
            .border(1.dp, if (on) Color.Transparent else Line, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(label, color = if (on) Bg else Muted,
            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Muted, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun MatchRow(m: AdminMatch, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(16.dp))
            .background(Card).border(1.dp, Line, RoundedCornerShape(16.dp))
            .clickable { onEdit() }.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (m.isLive) {
                    Box(Modifier.clip(RoundedCornerShape(5.dp)).background(Red)
                        .padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.width(6.dp))
                }
                Text("${m.homeName} × ${m.awayName}", color = TextC, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(3.dp))
            Text("${m.league} • ${m.servers.size} سيرفر • ${if (m.isLive) m.minute else m.kickoff}",
                color = Muted, fontSize = 11.sp)
        }
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Gold, modifier = Modifier.size(20.dp)) }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(20.dp)) }
    }
}

@Composable
private fun ChannelRow(c: AdminChannel, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(16.dp))
            .background(Card).border(1.dp, Line, RoundedCornerShape(16.dp))
            .clickable { onEdit() }.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(11.dp))
                .background(Brush.linearGradient(listOf(parseC(c.color1), parseC(c.color2)))),
            contentAlignment = Alignment.Center
        ) { Text(c.abbr, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(c.name, color = TextC, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${c.nowPlaying} • ${c.servers.size} سيرفر", color = Muted, fontSize = 11.sp)
        }
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Gold, modifier = Modifier.size(20.dp)) }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(20.dp)) }
    }
}

internal fun parseC(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) { Color(0xFF444444) }
