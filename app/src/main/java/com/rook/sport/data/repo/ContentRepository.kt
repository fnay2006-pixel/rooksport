package com.rook.sport.data.repo

import android.graphics.Color as AndroidColor
import com.rook.sport.data.RemoteConfigUrl
import com.rook.sport.data.model.*
import com.rook.sport.data.remote.*

/**
 * يجلب المباريات والقنوات من ملف JSON البعيد (تتحكم به أنت).
 * إذا فشل الاتصال، يرجع للبيانات التجريبية حتى لا يتعطّل التطبيق.
 */
object ContentRepository {

    data class Content(
        val matches: List<Match>,
        val channels: List<Channel>,
        val serversByOwner: Map<String, List<StreamServer>>,
        val notice: String? = null,
        val fromRemote: Boolean = false
    )

    suspend fun load(): Content {
        return try {
            val cfg = RemoteService.api.getConfig(RemoteConfigUrl.URL)
            mapConfig(cfg)
        } catch (e: Exception) {
            // فشل الشبكة أو الرابط غير مهيأ بعد → بيانات تجريبية
            demoContent()
        }
    }

    private fun mapConfig(cfg: RemoteConfig): Content {
        val servers = mutableMapOf<String, List<StreamServer>>()

        val matches = cfg.matches.map { m ->
            servers[m.id] = m.servers.toServers(m.id)
            Match(
                id = m.id,
                league = m.league,
                leagueShort = m.leagueShort.ifBlank { m.league },
                homeName = m.homeName, homeAbbr = m.homeAbbr, homeColor = parseColor(m.homeColor),
                awayName = m.awayName, awayAbbr = m.awayAbbr, awayColor = parseColor(m.awayColor),
                homeScore = m.homeScore, awayScore = m.awayScore,
                isLive = m.isLive, minute = m.minute,
                kickoff = m.kickoff, dayLabel = m.dayLabel,
                streamUrl = m.servers.firstOrNull()?.url
            )
        }

        val channels = cfg.channels.map { c ->
            servers[c.id] = c.servers.toServers(c.id)
            Channel(
                id = c.id, name = c.name, nowPlaying = c.nowPlaying,
                category = c.category, abbr = c.abbr,
                gradient = listOf(parseColor(c.color1), parseColor(c.color2)),
                quality = c.quality, isLive = c.isLive,
                streamUrl = c.servers.firstOrNull()?.url
            )
        }

        return Content(
            matches = matches.ifEmpty { DemoRepository.let { listOf(it.featuredMatch) + it.todayMatches } },
            channels = channels.ifEmpty { DemoRepository.channels },
            serversByOwner = servers,
            notice = cfg.notice,
            fromRemote = true
        )
    }

    private fun List<RemoteServer>.toServers(ownerId: String): List<StreamServer> =
        if (isEmpty()) DemoRepository.serversFor(null)
        else mapIndexed { i, s ->
            StreamServer(
                id = s.id.ifBlank { "s${i + 1}" },
                name = s.name,
                language = s.language,
                quality = s.quality,
                pingMs = 20 + i * 15,
                url = s.url
            )
        }

    private fun parseColor(hex: String): Long = try {
        (AndroidColor.parseColor(hex).toLong() and 0xFFFFFFFFL)
    } catch (e: Exception) {
        0xFF444444L
    }

    private fun demoContent(): Content {
        val matches = listOf(DemoRepository.featuredMatch) + DemoRepository.todayMatches
        val servers = (matches.map { it.id to DemoRepository.serversFor(it.streamUrl) } +
                DemoRepository.channels.map { it.id to DemoRepository.serversFor(it.streamUrl) }).toMap()
        return Content(
            matches = matches,
            channels = DemoRepository.channels,
            serversByOwner = servers,
            notice = null,
            fromRemote = false
        )
    }
}
