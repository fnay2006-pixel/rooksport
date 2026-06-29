package com.rook.sport.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rook.sport.data.model.StreamServer
import com.rook.sport.data.repo.DemoRepository
import com.rook.sport.ui.theme.*

/**
 * Live player screen powered by Media3 ExoPlayer.
 * Plays LEGAL HLS/DASH (m3u8) streams only.
 *
 * يدعم وضع ملء الشاشة (Landscape) مع إخفاء أشرطة النظام.
 */
@Composable
fun PlayerScreen(
    streamUrl: String,
    title: String,
    subtitle: String,
    servers: List<StreamServer>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity

    var currentUrl by remember { mutableStateOf(streamUrl) }
    val serverList = remember(servers) {
        servers.ifEmpty { DemoRepository.serversFor(streamUrl) }
    }
    var selectedServer by remember { mutableStateOf(serverList.first().id) }
    var quality by remember { mutableStateOf("Auto") }

    // حالة ملء الشاشة
    var isFullscreen by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(buildMediaItem(currentUrl))
            prepare()
            playWhenReady = true
        }
    }

    // تبديل البث عند تغيير السيرفر
    LaunchedEffect(currentUrl) {
        exoPlayer.setMediaItem(buildMediaItem(currentUrl))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // التحكم بالاتجاه + أشرطة النظام عند تبديل ملء الشاشة
    LaunchedEffect(isFullscreen) {
        activity ?: return@LaunchedEffect
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (isFullscreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // إدارة دورة الحياة + التنظيف عند الخروج
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
            // استعادة الوضع الطبيعي عند مغادرة الشاشة
            activity?.let {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                it.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                WindowInsetsControllerCompat(it.window, it.window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // زر الرجوع في وضع ملء الشاشة يخرج منه أولاً
    BackHandler(enabled = isFullscreen) { isFullscreen = false }

    // ===== مكوّن الفيديو (مشترك بين الوضعين) =====
    val videoPlayer = @Composable { fullscreen: Boolean ->
        Box(
            Modifier
                .fillMaxWidth()
                .then(if (fullscreen) Modifier.fillMaxHeight() else Modifier.height(230.dp))
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            // زر الرجوع (يظهر فقط في الوضع العمودي عبر الزاوية)
            if (!fullscreen) {
                CircleIconBtn(Icons.AutoMirrored.Filled.ArrowBack, Alignment.TopEnd) { onBack() }
            }
            // زر ملء الشاشة / الخروج منه
            CircleIconBtn(
                if (fullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                Alignment.BottomStart
            ) { isFullscreen = !fullscreen }
        }
    }

    // ===== الوضع الأفقي (ملء الشاشة الكامل) =====
    if (isFullscreen) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            videoPlayer(true)
        }
        return
    }

    // ===== الوضع العمودي العادي =====
    LazyColumn(Modifier.fillMaxSize().background(ZonaBg)) {
        item { videoPlayer(false) }
        item {
            Column(Modifier.padding(16.dp)) {
                Text(title, color = ZonaText, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = ZonaMuted, fontSize = 12.sp)
            }
        }
        item {
            Row(Modifier.padding(16.dp, 0.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Auto", "1080p", "720p", "480p").forEach { q ->
                    val on = q == quality
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp))
                            .background(if (on) ZonaGold else ZonaCard)
                            .border(1.dp, if (on) Color.Transparent else ZonaLine, RoundedCornerShape(10.dp))
                            .clickable { quality = q }.padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(q, color = if (on) ZonaBg else ZonaMuted,
                            fontWeight = if (on) FontWeight.ExtraBold else FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Text("سيرفرات البث", color = ZonaText, fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp, modifier = Modifier.padding(16.dp, 18.dp, 16.dp, 10.dp))
        }
        items(serverList.size) { i ->
            val s = serverList[i]
            ServerRow(s, s.id == selectedServer) {
                selectedServer = s.id
                currentUrl = s.url
            }
        }
        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
private fun BoxScope.CircleIconBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    align: Alignment,
    onClick: () -> Unit
) {
    Box(
        Modifier.align(align).padding(14.dp).size(38.dp)
            .clip(CircleShape).background(Color.Black.copy(0.5f)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
}

/**
 * يبني عنصر تشغيل ويحدد نوع البث.
 * يفرض نوع HLS (m3u8) صراحةً حتى لو لم ينتهِ الرابط بـ .m3u8،
 * مع دعم تلقائي لبقية الصيغ (mp4 / dash...).
 */
private fun buildMediaItem(url: String): MediaItem {
    val builder = MediaItem.Builder().setUri(url)
    if (url.contains("m3u8", ignoreCase = true)) {
        builder.setMimeType(MimeTypes.APPLICATION_M3U8)
    }
    return builder.build()
}

@Composable
private fun ServerRow(s: StreamServer, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.padding(16.dp, 5.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(ZonaCard)
            .border(1.dp, if (selected) ZonaGold else ZonaLine, RoundedCornerShape(14.dp))
            .clickable { onClick() }.padding(14.dp, 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF1B2A52)),
            contentAlignment = Alignment.Center
        ) { Text(s.id.uppercase(), color = ZonaAccent, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(s.name, color = ZonaText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(s.language, color = ZonaMuted, fontSize = 10.sp)
        }
        val pingColor = if (s.pingMs < 50) ZonaAccent else ZonaGold
        Text("● ${s.pingMs}ms", color = pingColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
