package com.rook.sport.data.model

import androidx.compose.ui.graphics.Color

/** A live or upcoming match. */
data class Match(
    val id: String,
    val league: String,
    val leagueShort: String,
    val homeName: String,
    val homeAbbr: String,
    val homeColor: Long,
    val awayName: String,
    val awayAbbr: String,
    val awayColor: Long,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val isLive: Boolean = false,
    val minute: String? = null,   // e.g. "67'"
    val kickoff: String? = null,  // e.g. "21:00"
    val dayLabel: String? = null, // e.g. "اليوم"
    val streamUrl: String? = null // legal HLS/DASH url
)

/** A TV channel (legal / open source streams only). */
data class Channel(
    val id: String,
    val name: String,
    val nowPlaying: String,
    val category: String,
    val abbr: String,
    val gradient: List<Long>,
    val quality: String = "HD",   // HD / 4K / FHD
    val isLive: Boolean = true,
    val streamUrl: String? = null // legal HLS url, e.g. open FAST channels
)

/** A streaming server option for a match/channel. */
data class StreamServer(
    val id: String,
    val name: String,
    val language: String,
    val quality: String,
    val pingMs: Int,
    val url: String
)

/** A timeline event inside a match. */
data class MatchEvent(
    val minute: String,
    val icon: String,
    val title: String,
    val subtitle: String
)

/** Category chip. */
data class Category(val id: String, val label: String, val emoji: String)
