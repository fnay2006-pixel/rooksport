package com.rook.sport.data.repo

import com.rook.sport.data.model.*

/**
 * DemoRepository — sample data for the UI.
 *
 * IMPORTANT (legal note):
 *  - The streamUrl values below point ONLY to free, open / royalty-free test
 *    HLS streams (Mux / Apple sample / Big Buck Bunny). They are placeholders.
 *  - To go live you must plug in LEGAL sources only:
 *      • A licensed sports-data API for scores/schedule
 *        (e.g. API-Football, TheSportsDB, football-data.org).
 *      • Officially free FAST channels, public-domain, or rights-cleared streams.
 *  - Do NOT inject pirated beIN / premium channel streams here.
 */
object DemoRepository {

    // Free / royalty-free HLS test streams (safe placeholders)
    private const val SAMPLE_HLS_1 =
        "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    private const val SAMPLE_HLS_2 =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"
    private const val SAMPLE_HLS_3 =
        "https://test-streams.mux.dev/test_001/stream.m3u8"

    val categories = listOf(
        Category("all", "الكل", "🔥"),
        Category("football", "كرة القدم", "⚽"),
        Category("basket", "كرة السلة", "🏀"),
        Category("tennis", "تنس", "🎾"),
        Category("f1", "فورمولا 1", "🏎️"),
        Category("boxing", "ملاكمة", "🥊"),
    )

    val featuredMatch = Match(
        id = "m1",
        league = "دوري أبطال أوروبا",
        leagueShort = "UCL",
        homeName = "ريال مدريد", homeAbbr = "RM", homeColor = 0xFFC8102E,
        awayName = "برشلونة", awayAbbr = "FC", awayColor = 0xFF004D98,
        homeScore = 2, awayScore = 1,
        isLive = true, minute = "67'",
        streamUrl = SAMPLE_HLS_1
    )

    val todayMatches = listOf(
        Match(
            id = "m2", league = "الدوري الإنجليزي", leagueShort = "البريميرليغ",
            homeName = "ليفربول", homeAbbr = "LI", homeColor = 0xFFC8102E,
            awayName = "مان سيتي", awayAbbr = "MC", awayColor = 0xFF6CABDD,
            kickoff = "21:00", dayLabel = "اليوم", streamUrl = SAMPLE_HLS_2
        ),
        Match(
            id = "m3", league = "الدوري الإيطالي", leagueShort = "السيري آ",
            homeName = "يوفنتوس", homeAbbr = "JU", homeColor = 0xFF000000,
            awayName = "نابولي", awayAbbr = "NA", awayColor = 0xFF0068A8,
            kickoff = "23:45", dayLabel = "اليوم", streamUrl = SAMPLE_HLS_3
        ),
        Match(
            id = "m4", league = "الدوري الألماني", leagueShort = "البوندسليغا",
            homeName = "بايرن ميونخ", homeAbbr = "BY", homeColor = 0xFFE30613,
            awayName = "دورتموند", awayAbbr = "DO", awayColor = 0xFFFDE100,
            kickoff = "19:30", dayLabel = "غداً", streamUrl = SAMPLE_HLS_1
        ),
    )

    val channels = listOf(
        Channel("c1", "الرياضية 1 HD", "الدوري الإنجليزي — مباشر", "رياضة", "S1",
            listOf(0xFF5B2A86, 0xFF2D1245), "HD", true, SAMPLE_HLS_1),
        Channel("c2", "الرياضية 2 4K", "الدوري الإسباني — مباشر", "رياضة", "S2",
            listOf(0xFF0A5C4A, 0xFF03281F), "4K", true, SAMPLE_HLS_2),
        Channel("c3", "الأخبار الرياضية", "استوديو التحليل", "أخبار", "NW",
            listOf(0xFF1D4E89, 0xFF0A2342), "HD", false, SAMPLE_HLS_3),
        Channel("c4", "قناة F1", "جائزة موناكو الكبرى", "رياضة", "F1",
            listOf(0xFFA8500A, 0xFF5E2C05), "HD", true, SAMPLE_HLS_1),
        Channel("c5", "قناة الملاكمة", "نزال البطولة", "رياضة", "BX",
            listOf(0xFF7A0A1D, 0xFF3D050E), "HD", false, SAMPLE_HLS_2),
        Channel("c6", "قناة الوثائقيات", "أساطير كرة القدم", "وثائقي", "DC",
            listOf(0xFF143A5C, 0xFF071C2E), "HD", false, SAMPLE_HLS_3),
    )

    fun serversFor(streamUrl: String?): List<StreamServer> = listOf(
        StreamServer("s1", "السيرفر الأول — HD", "عربي • معلق محمد", "1080p", 24,
            streamUrl ?: SAMPLE_HLS_1),
        StreamServer("s2", "السيرفر الثاني — FHD", "إنجليزي", "1080p", 38, SAMPLE_HLS_2),
        StreamServer("s3", "سيرفر احتياطي", "عربي", "720p", 65, SAMPLE_HLS_3),
    )

    val featuredEvents = listOf(
        MatchEvent("67'", "⚽", "فينيسيوس جونيور", "هدف — ريال مدريد"),
        MatchEvent("54'", "🟨", "جافي", "بطاقة صفراء — برشلونة"),
        MatchEvent("41'", "⚽", "ليفاندوفسكي", "هدف — برشلونة"),
        MatchEvent("23'", "⚽", "بيلينغهام", "هدف — ريال مدريد"),
    )
}
