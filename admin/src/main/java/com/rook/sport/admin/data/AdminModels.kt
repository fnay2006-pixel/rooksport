package com.rook.sport.admin.data

/** نموذج مباراة في لوحة الأدمن. */
data class AdminMatch(
    val id: String = "",
    val league: String = "",
    val leagueShort: String = "",
    val homeName: String = "",
    val homeAbbr: String = "",
    val homeColor: String = "#C8102E",
    val awayName: String = "",
    val awayAbbr: String = "",
    val awayColor: String = "#004D98",
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val isLive: Boolean = false,
    val minute: String = "",
    val kickoff: String = "",
    val dayLabel: String = "اليوم",
    val servers: List<AdminServer> = emptyList()
)

/** نموذج قناة في لوحة الأدمن. */
data class AdminChannel(
    val id: String = "",
    val name: String = "",
    val nowPlaying: String = "",
    val category: String = "رياضة",
    val abbr: String = "",
    val color1: String = "#5B2A86",
    val color2: String = "#2D1245",
    val quality: String = "HD",
    val isLive: Boolean = true,
    val servers: List<AdminServer> = emptyList()
)

/** سيرفر بث (رابط m3u8). */
data class AdminServer(
    val id: String = "s1",
    val name: String = "السيرفر الأول",
    val language: String = "عربي",
    val quality: String = "HD",
    val url: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id, "name" to name, "language" to language,
        "quality" to quality, "url" to url
    )
}
