package com.rook.sport.admin.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * مستودع الأدمن: يقرأ ويكتب ويحذف المباريات والقنوات في Firebase Firestore.
 * أي تعديل هنا يظهر فوراً في تطبيق المستخدم.
 */
object AdminRepository {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    // ===== المباريات =====
    suspend fun getMatches(): List<AdminMatch> {
        return db.collection("matches").get().await().documents.map { d ->
            AdminMatch(
                id = d.id,
                league = d.getString("league") ?: "",
                leagueShort = d.getString("leagueShort") ?: "",
                homeName = d.getString("homeName") ?: "",
                homeAbbr = d.getString("homeAbbr") ?: "",
                homeColor = d.getString("homeColor") ?: "#C8102E",
                awayName = d.getString("awayName") ?: "",
                awayAbbr = d.getString("awayAbbr") ?: "",
                awayColor = d.getString("awayColor") ?: "#004D98",
                homeScore = d.getLong("homeScore")?.toInt(),
                awayScore = d.getLong("awayScore")?.toInt(),
                isLive = d.getBoolean("isLive") ?: false,
                minute = d.getString("minute") ?: "",
                kickoff = d.getString("kickoff") ?: "",
                dayLabel = d.getString("dayLabel") ?: "اليوم",
                servers = parseServers(d.get("servers"))
            )
        }
    }

    suspend fun saveMatch(m: AdminMatch) {
        val data = hashMapOf<String, Any?>(
            "league" to m.league,
            "leagueShort" to m.leagueShort.ifBlank { m.league },
            "homeName" to m.homeName, "homeAbbr" to m.homeAbbr, "homeColor" to m.homeColor,
            "awayName" to m.awayName, "awayAbbr" to m.awayAbbr, "awayColor" to m.awayColor,
            "homeScore" to m.homeScore, "awayScore" to m.awayScore,
            "isLive" to m.isLive, "minute" to m.minute,
            "kickoff" to m.kickoff, "dayLabel" to m.dayLabel,
            "servers" to m.servers.map { it.toMap() },
            "updatedAt" to System.currentTimeMillis()
        )
        if (m.id.isBlank()) {
            db.collection("matches").add(data).await()
        } else {
            db.collection("matches").document(m.id).set(data).await()
        }
    }

    suspend fun deleteMatch(id: String) {
        db.collection("matches").document(id).delete().await()
    }

    // ===== القنوات =====
    suspend fun getChannels(): List<AdminChannel> {
        return db.collection("channels").get().await().documents.map { d ->
            AdminChannel(
                id = d.id,
                name = d.getString("name") ?: "",
                nowPlaying = d.getString("nowPlaying") ?: "",
                category = d.getString("category") ?: "رياضة",
                abbr = d.getString("abbr") ?: "",
                color1 = d.getString("color1") ?: "#5B2A86",
                color2 = d.getString("color2") ?: "#2D1245",
                quality = d.getString("quality") ?: "HD",
                isLive = d.getBoolean("isLive") ?: true,
                servers = parseServers(d.get("servers"))
            )
        }
    }

    suspend fun saveChannel(c: AdminChannel) {
        val data = hashMapOf<String, Any?>(
            "name" to c.name, "nowPlaying" to c.nowPlaying, "category" to c.category,
            "abbr" to c.abbr, "color1" to c.color1, "color2" to c.color2,
            "quality" to c.quality, "isLive" to c.isLive,
            "servers" to c.servers.map { it.toMap() },
            "updatedAt" to System.currentTimeMillis()
        )
        if (c.id.isBlank()) {
            db.collection("channels").add(data).await()
        } else {
            db.collection("channels").document(c.id).set(data).await()
        }
    }

    suspend fun deleteChannel(id: String) {
        db.collection("channels").document(id).delete().await()
    }

    // ===== إعداد عام =====
    suspend fun setNotice(text: String) {
        db.collection("settings").document("general")
            .set(mapOf("notice" to text)).await()
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseServers(raw: Any?): List<AdminServer> {
        val list = raw as? List<Map<String, Any?>> ?: return emptyList()
        return list.map { m ->
            AdminServer(
                id = (m["id"] as? String) ?: "s1",
                name = (m["name"] as? String) ?: "",
                language = (m["language"] as? String) ?: "",
                quality = (m["quality"] as? String) ?: "HD",
                url = (m["url"] as? String) ?: ""
            )
        }
    }
}
