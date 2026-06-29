package com.rook.sport.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rook.sport.data.model.*
import com.rook.sport.data.repo.ContentRepository
import com.rook.sport.data.repo.DemoRepository
import kotlinx.coroutines.tasks.await

/**
 * يقرأ المباريات والقنوات من Firebase Firestore (التي يعدّلها الأدمن).
 *
 * بنية قاعدة البيانات في Firestore:
 *   matches (collection)  → كل وثيقة = مباراة
 *   channels (collection) → كل وثيقة = قناة
 *   settings/general      → إعدادات عامة (notice)
 */
object FirebaseRepository {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    suspend fun load(): ContentRepository.Content {
        return try {
            val matchDocs = db.collection("matches")
                .get().await().documents

            val channelDocs = db.collection("channels")
                .get().await().documents

            val notice = try {
                db.collection("settings").document("general")
                    .get().await().getString("notice")
            } catch (e: Exception) { null }

            val servers = mutableMapOf<String, List<StreamServer>>()

            val matches = matchDocs.mapNotNull { d ->
                val id = d.id
                val srv = parseServers(d.get("servers"), id)
                servers[id] = srv
                Match(
                    id = id,
                    league = d.getString("league") ?: "",
                    leagueShort = d.getString("leagueShort") ?: d.getString("league") ?: "",
                    homeName = d.getString("homeName") ?: "",
                    homeAbbr = d.getString("homeAbbr") ?: "",
                    homeColor = parseColor(d.getString("homeColor")),
                    awayName = d.getString("awayName") ?: "",
                    awayAbbr = d.getString("awayAbbr") ?: "",
                    awayColor = parseColor(d.getString("awayColor")),
                    homeScore = (d.getLong("homeScore"))?.toInt(),
                    awayScore = (d.getLong("awayScore"))?.toInt(),
                    isLive = d.getBoolean("isLive") ?: false,
                    minute = d.getString("minute"),
                    kickoff = d.getString("kickoff"),
                    dayLabel = d.getString("dayLabel"),
                    streamUrl = srv.firstOrNull()?.url
                )
            }

            val channels = channelDocs.mapNotNull { d ->
                val id = d.id
                val srv = parseServers(d.get("servers"), id)
                servers[id] = srv
                Channel(
                    id = id,
                    name = d.getString("name") ?: "",
                    nowPlaying = d.getString("nowPlaying") ?: "",
                    category = d.getString("category") ?: "رياضة",
                    abbr = d.getString("abbr") ?: "",
                    gradient = listOf(
                        parseColor(d.getString("color1")),
                        parseColor(d.getString("color2"))
                    ),
                    quality = d.getString("quality") ?: "HD",
                    isLive = d.getBoolean("isLive") ?: true,
                    streamUrl = srv.firstOrNull()?.url
                )
            }

            // إن كانت القاعدة فارغة، استخدم البيانات التجريبية
            if (matches.isEmpty() && channels.isEmpty()) {
                ContentRepository.load()
            } else {
                ContentRepository.Content(
                    matches = matches,
                    channels = channels,
                    serversByOwner = servers,
                    notice = notice,
                    fromRemote = true
                )
            }
        } catch (e: Exception) {
            // فشل الاتصال بـ Firebase → fallback
            ContentRepository.load()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseServers(raw: Any?, ownerId: String): List<StreamServer> {
        val list = raw as? List<Map<String, Any?>> ?: return DemoRepository.serversFor(null)
        if (list.isEmpty()) return DemoRepository.serversFor(null)
        return list.mapIndexed { i, m ->
            StreamServer(
                id = (m["id"] as? String) ?: "s${i + 1}",
                name = (m["name"] as? String) ?: "السيرفر ${i + 1}",
                language = (m["language"] as? String) ?: "",
                quality = (m["quality"] as? String) ?: "HD",
                pingMs = 20 + i * 15,
                url = (m["url"] as? String) ?: ""
            )
        }.filter { it.url.isNotBlank() }
    }

    private fun parseColor(hex: String?): Long = try {
        (android.graphics.Color.parseColor(hex ?: "#444444").toLong() and 0xFFFFFFFFL)
    } catch (e: Exception) { 0xFF444444L }
}
