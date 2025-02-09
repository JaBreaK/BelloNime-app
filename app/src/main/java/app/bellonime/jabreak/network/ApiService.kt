package app.bellonime.jabreak.network

import app.bellonime.jabreak.model.Anime
import app.bellonime.jabreak.model.AnimeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class ApiResponse(val data: AnimeList)
data class AnimeList(val recent: RecentAnime)
data class RecentAnime(val animeList: List<Anime>)

interface ApiService {
    @GET("samehadaku/home")
    fun getRecentAnime(): Call<ApiResponse>

    @GET("samehadaku/anime/{animeId}")
    fun getAnimeDetail(@Path("animeId") animeId: String): Call<AnimeDetailResponse>

    // Method baru untuk detail episode
    @GET("samehadaku/episode/{episodeId}")
    fun getEpisodeDetail(@Path("episodeId") episodeId: String): Call<EpisodeDetailResponse>

    @GET("samehadaku/server/{serverId}")
    fun getVideoUrl(@Path("serverId") serverId: String): Call<ServerResponse>

}
data class AnimeDetailResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: AnimeDetail
)
data class ServerResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: VideoData
)

data class VideoData(
    val url: String
)

data class Data(
    val url: String,
    val qualities: List<Quality> // List of available qualities
)

data class Quality(
    val name: String,      // e.g. "480p"
    val serverId: String   // Unique ID for the server (e.g. "96EBA-A-xhmyrq")
)
