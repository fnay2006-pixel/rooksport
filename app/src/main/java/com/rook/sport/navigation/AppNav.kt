package com.rook.sport.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rook.sport.data.ContentViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rook.sport.data.repo.DemoRepository
import com.rook.sport.player.PlayerScreen
import com.rook.sport.ui.screens.ChannelsScreen
import com.rook.sport.ui.screens.HomeScreen
import com.rook.sport.ui.screens.MatchDetailScreen
import com.rook.sport.ui.theme.*

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Tab("home", "الرئيسية", Icons.Default.Home)
    data object Channels : Tab("channels", "القنوات", Icons.Default.LiveTv)
    data object Matches : Tab("matches", "المباريات", Icons.Default.CalendarMonth)
    data object Profile : Tab("profile", "حسابي", Icons.Default.Person)
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val tabs = listOf(Tab.Home, Tab.Channels, Tab.Matches, Tab.Profile)
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = currentRoute in tabs.map { it.route }

    // ViewModel يحمل المحتوى المجلوب من ملف JSON البعيد
    val vm: ContentViewModel = viewModel()
    val content by vm.state.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        containerColor = ZonaBg,
        bottomBar = { if (showBar) BottomBar(nav, tabs, currentRoute) }
    ) { pad ->
        if (loading && content.matches.isEmpty() && content.channels.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = ZonaGold)
                    Spacer(Modifier.height(14.dp))
                    Text("جاري تحميل المباريات...", color = ZonaMuted, fontSize = 13.sp)
                }
            }
            return@Scaffold
        }
        NavHost(nav, startDestination = Tab.Home.route, modifier = Modifier.padding(pad)) {
            composable(Tab.Home.route) {
                HomeScreen(
                    matches = content.matches,
                    channels = content.channels,
                    notice = content.notice,
                    onWatch = { id -> nav.navigate("player/$id") },
                    onMatch = { id -> nav.navigate("match/$id") },
                    onChannel = { id -> nav.navigate("player/$id") }
                )
            }
            composable(Tab.Channels.route) {
                ChannelsScreen(content.channels, onChannel = { id -> nav.navigate("player/$id") })
            }
            composable(Tab.Matches.route) {
                HomeScreen(
                    matches = content.matches,
                    channels = content.channels,
                    notice = content.notice,
                    onWatch = { id -> nav.navigate("player/$id") },
                    onMatch = { id -> nav.navigate("match/$id") },
                    onChannel = { id -> nav.navigate("player/$id") }
                )
            }
            composable(Tab.Profile.route) { ProfilePlaceholder(content.fromRemote) { vm.refresh() } }

            composable("match/{id}") { entry ->
                val id = entry.arguments?.getString("id") ?: ""
                MatchDetailScreen(vm.matchById(id), onBack = { nav.popBackStack() },
                    onWatch = { mid -> nav.navigate("player/$mid") })
            }
            composable("player/{id}") { entry ->
                val id = entry.arguments?.getString("id") ?: ""
                val match = vm.matchById(id)
                val channel = vm.channelById(id)
                val servers = vm.serversFor(id)
                val url = servers.firstOrNull()?.url
                    ?: match?.streamUrl ?: channel?.streamUrl
                    ?: DemoRepository.featuredMatch.streamUrl!!
                val title = match?.let { "${it.homeName} × ${it.awayName}" } ?: channel?.name ?: "بث مباشر"
                val sub = match?.league ?: channel?.nowPlaying ?: ""
                PlayerScreen(url, title, sub, servers, onBack = { nav.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomBar(nav: androidx.navigation.NavHostController, tabs: List<Tab>, current: String?) {
    Row(
        Modifier.fillMaxWidth().background(ZonaBg2).border(1.dp, ZonaLine)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val selected = current == tab.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    nav.navigate(tab.route) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Icon(tab.icon, null, tint = if (selected) ZonaGold else ZonaMuted,
                    modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(4.dp))
                Text(tab.label, color = if (selected) ZonaGold else ZonaMuted,
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfilePlaceholder(fromRemote: Boolean, onRefresh: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(ZonaBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(80.dp).clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(ZonaGold, ZonaGold2))),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Person, null, tint = ZonaBg, modifier = Modifier.size(40.dp)) }
            Spacer(Modifier.height(16.dp))
            Text("حسابي", color = ZonaText, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text("سجّل الدخول لحفظ المفضلة والإشعارات", color = ZonaMuted, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            // حالة الاتصال بلوحة التحكم
            Box(
                Modifier.clip(RoundedCornerShape(20.dp))
                    .background(if (fromRemote) ZonaAccent.copy(0.12f) else ZonaRed.copy(0.12f))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    if (fromRemote) "● متصل بالخادم — يعرض بياناتك" else "● وضع تجريبي (لم يُضبط الرابط بعد)",
                    color = if (fromRemote) ZonaAccent else ZonaRed,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(ZonaGold, ZonaGold2)))
                    .clickable { onRefresh() }.padding(horizontal = 22.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Refresh, null, tint = ZonaBg, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("تحديث المحتوى", color = ZonaBg, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }
    }
}
