package app.bellonime.jabreak.network

import app.bellonime.jabreak.model.Anime
import app.bellonime.jabreak.model.AnimeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Data class untuk response home
data class ApiResponse(val data: AnimeList)
data class AnimeList(val recent: RecentAnime)
data class RecentAnime(val animeList: List<Anime>)

// Retrofit API service interface
interface ApiService {

    @GET("samehadaku/home")
    fun getRecentAnime(): Call<ApiResponse>

    @GET("samehadaku/anime/{animeId}")
    fun getAnimeDetail(@Path("animeId") animeId: String): Call<AnimeDetailResponse>

    // Method baru untuk detail episode
    @GET("samehadaku/episode/{episodeId}")
    fun getEpisodeDetail(@Path("episodeId") episodeId: String): Call<EpisodeDetailResponse>

    // Method untuk mengambil URL video dari server
    @GET("samehadaku/server/{serverId}")
    fun getVideoUrl(@Path("serverId") serverId: String): Call<ServerResponse>
}

// Response untuk Anime Detail
data class AnimeDetailResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: AnimeDetail
)

// Response untuk video URL dari server
data class ServerResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: VideoData
)

// Data class untuk menyimpan URL video
data class VideoData(
    val url: String
)

// Contoh data tambahan (misalnya untuk detail episode) jika diperlukan
data class Data(
    val url: String,
    val qualities: List<Quality> // List of available qualities
)

// Data class untuk kualitas video, misalnya "480p", "720p", dll.
data class Quality(
    val name: String,      // e.g. "480p"
    val serverId: String,
    val serverList: List<Server>?
)

data class Server(
    val title: String?,
    val serverId: String,
    val href: String?
)
