package com.rook.sport.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.rook.sport.admin.data.AdminChannel
import com.rook.sport.admin.data.AdminMatch
import com.rook.sport.admin.data.AdminServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun field(value: String, label: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, fontSize = 12.sp) }, singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold, unfocusedBorderColor = Line,
            focusedLabelColor = Gold, unfocusedLabelColor = Muted, cursorColor = Gold,
            focusedTextColor = TextC, unfocusedTextColor = TextC,
            focusedContainerColor = Card, unfocusedContainerColor = Card
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun EditorScaffold(title: String, onCancel: () -> Unit, onSave: () -> Unit, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg)) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextC) }
            Text(title, color = TextC, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            Box(
                Modifier.clip(RoundedCornerShape(11.dp))
                    .background(Brush.linearGradient(listOf(Gold, Gold2)))
                    .clickable { onSave() }.padding(horizontal = 18.dp, vertical = 9.dp)
            ) { Text("حفظ", color = Bg, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp) }
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 40.dp)) {
            item { content() }
        }
    }
}

@Composable
fun MatchEditor(initial: AdminMatch, onCancel: () -> Unit, onSave: (AdminMatch) -> Unit) {
    var m by remember { mutableStateOf(initial) }
    var servers by remember { mutableStateOf(initial.servers.ifEmpty { listOf(AdminServer()) }) }
    val isNew = initial.id.isBlank()

    EditorScaffold(if (isNew) "إضافة مباراة" else "تعديل مباراة", onCancel,
        onSave = { onSave(m.copy(servers = servers.filter { it.url.isNotBlank() })) }) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            field(m.league, "البطولة", { m = m.copy(league = it) })
            field(m.leagueShort, "اختصار البطولة", { m = m.copy(leagueShort = it) })
            Text("الفريق الأول", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field(m.homeName, "الاسم", { m = m.copy(homeName = it) }, Modifier.weight(2f))
                field(m.homeAbbr, "اختصار", { m = m.copy(homeAbbr = it) }, Modifier.weight(1f))
            }
            field(m.homeColor, "لون الفريق الأول (مثل #C8102E)", { m = m.copy(homeColor = it) })
            Text("الفريق الثاني", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field(m.awayName, "الاسم", { m = m.copy(awayName = it) }, Modifier.weight(2f))
                field(m.awayAbbr, "اختصار", { m = m.copy(awayAbbr = it) }, Modifier.weight(1f))
            }
            field(m.awayColor, "لون الفريق الثاني (مثل #004D98)", { m = m.copy(awayColor = it) })

            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("مباشر الآن؟", color = TextC, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Switch(checked = m.isLive, onCheckedChange = { m = m.copy(isLive = it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Gold, checkedTrackColor = Gold.copy(0.4f)))
            }
            if (m.isLive) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    field(m.minute, "الدقيقة (67')", { m = m.copy(minute = it) }, Modifier.weight(1f))
                    field(m.homeScore?.toString() ?: "", "نتيجة 1", { m = m.copy(homeScore = it.toIntOrNull()) }, Modifier.weight(1f))
                    field(m.awayScore?.toString() ?: "", "نتيجة 2", { m = m.copy(awayScore = it.toIntOrNull()) }, Modifier.weight(1f))
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    field(m.kickoff, "وقت البداية (21:00)", { m = m.copy(kickoff = it) }, Modifier.weight(1f))
                    field(m.dayLabel, "اليوم/غداً", { m = m.copy(dayLabel = it) }, Modifier.weight(1f))
                }
            }

            ServersEditor(servers) { servers = it }
        }
    }
}

@Composable
fun ChannelEditor(initial: AdminChannel, onCancel: () -> Unit, onSave: (AdminChannel) -> Unit) {
    var c by remember { mutableStateOf(initial) }
    var servers by remember { mutableStateOf(initial.servers.ifEmpty { listOf(AdminServer()) }) }
    val isNew = initial.id.isBlank()

    EditorScaffold(if (isNew) "إضافة قناة" else "تعديل قناة", onCancel,
        onSave = { onSave(c.copy(servers = servers.filter { it.url.isNotBlank() })) }) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            field(c.name, "اسم القناة", { c = c.copy(name = it) })
            field(c.nowPlaying, "المعروض الآن", { c = c.copy(nowPlaying = it) })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field(c.category, "التصنيف", { c = c.copy(category = it) }, Modifier.weight(1f))
                field(c.abbr, "اختصار", { c = c.copy(abbr = it) }, Modifier.weight(1f))
                field(c.quality, "الجودة", { c = c.copy(quality = it) }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field(c.color1, "لون 1", { c = c.copy(color1 = it) }, Modifier.weight(1f))
                field(c.color2, "لون 2", { c = c.copy(color2 = it) }, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("مباشر؟", color = TextC, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Switch(checked = c.isLive, onCheckedChange = { c = c.copy(isLive = it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Gold, checkedTrackColor = Gold.copy(0.4f)))
            }
            ServersEditor(servers) { servers = it }
        }
    }
}

@Composable
private fun ServersEditor(servers: List<AdminServer>, onChange: (List<AdminServer>) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("سيرفرات البث (m3u8)", color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp,
            modifier = Modifier.weight(1f))
        Box(
            Modifier.clip(RoundedCornerShape(9.dp)).background(Accent.copy(0.15f))
                .clickable { onChange(servers + AdminServer(id = "s${servers.size + 1}")) }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = Accent, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text("سيرفر", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    servers.forEachIndexed { i, s ->
        Column(
            Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(12.dp))
                .background(Card2).border(1.dp, Line, RoundedCornerShape(12.dp)).padding(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("سيرفر ${i + 1}", color = TextC, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (servers.size > 1) IconButton(onClick = {
                    onChange(servers.toMutableList().also { it.removeAt(i) })
                }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            field(s.name, "اسم السيرفر", { v ->
                onChange(servers.toMutableList().also { it[i] = s.copy(name = v) })
            })
            Spacer(Modifier.height(8.dp))
            field(s.url, "رابط m3u8", { v ->
                onChange(servers.toMutableList().also { it[i] = s.copy(url = v) })
            })
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field(s.language, "اللغة", { v ->
                    onChange(servers.toMutableList().also { it[i] = s.copy(language = v) })
                }, Modifier.weight(1f))
                field(s.quality, "الجودة", { v ->
                    onChange(servers.toMutableList().also { it[i] = s.copy(quality = v) })
                }, Modifier.weight(1f))
            }
        }
    }
}
