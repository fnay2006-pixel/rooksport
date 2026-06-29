package com.rook.sport.data.remote

import com.google.gson.annotations.SerializedName

/**
 * نماذج البيانات القادمة من ملف JSON البعيد (Remote Config).
 *
 * أنت تتحكم بالكامل في هذا الملف من الإنترنت:
 *  - أضف/احذف مباريات
 *  - حدّث روابط m3u8
 *  - فعّل/عطّل قنوات
 * كل ذلك بدون تحديث التطبيق إطلاقاً.
 */
data class RemoteConfig(
    @SerializedName("app_name") val appName: String? = null,
    @SerializedName("force_update") val forceUpdate: Boolean = false,
    @SerializedName("notice") val notice: String? = null,          // شريط إعلان اختياري
    @SerializedName("matches") val matches: List<RemoteMatch> = emptyList(),
    @SerializedName("channels") val channels: List<RemoteChannel> = emptyList()
)

data class RemoteMatch(
    @SerializedName("id") val id: String,
    @SerializedName("league") val league: String = "",
    @SerializedName("league_short") val leagueShort: String = "",
    @SerializedName("home_name") val homeName: String = "",
    @SerializedName("home_abbr") val homeAbbr: String = "",
    @SerializedName("home_color") val homeColor: String = "#444444",
    @SerializedName("away_name") val awayName: String = "",
    @SerializedName("away_abbr") val awayAbbr: String = "",
    @SerializedName("away_color") val awayColor: String = "#444444",
    @SerializedName("home_score") val homeScore: Int? = null,
    @SerializedName("away_score") val awayScore: Int? = null,
    @SerializedName("is_live") val isLive: Boolean = false,
    @SerializedName("minute") val minute: String? = null,
    @SerializedName("kickoff") val kickoff: String? = null,
    @SerializedName("day_label") val dayLabel: String? = null,
    // قائمة سيرفرات m3u8 لكل مباراة
    @SerializedName("servers") val servers: List<RemoteServer> = emptyList()
)

data class RemoteChannel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String = "",
    @SerializedName("now_playing") val nowPlaying: String = "",
    @SerializedName("category") val category: String = "رياضة",
    @SerializedName("abbr") val abbr: String = "",
    @SerializedName("color1") val color1: String = "#1B2A52",
    @SerializedName("color2") val color2: String = "#0D1426",
    @SerializedName("quality") val quality: String = "HD",
    @SerializedName("is_live") val isLive: Boolean = true,
    @SerializedName("servers") val servers: List<RemoteServer> = emptyList()
)

data class RemoteServer(
    @SerializedName("id") val id: String = "s1",
    @SerializedName("name") val name: String = "السيرفر",
    @SerializedName("language") val language: String = "",
    @SerializedName("quality") val quality: String = "HD",
    @SerializedName("url") val url: String   // رابط m3u8 / HLS
)
