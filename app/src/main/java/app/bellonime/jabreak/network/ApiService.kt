package app.bellonime.jabreak.network

import app.bellonime.jabreak.model.Anime
import app.bellonime.jabreak.model.AnimeCategory
import app.bellonime.jabreak.model.AnimeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import app.bellonime.jabreak.model.ScheduleResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Data class untuk response home

data class ApiResponse(
    val statusCode: Int,
    val data: AnimeList
)


data class ListItem(
    val startWith: String,
    val animeList: List<Anime>
)
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

    @GET("samehadaku/schedule")
    fun getSchedule(): Call<ScheduleResponse>

    @GET("samehadaku/anime")
    suspend fun getAnimeList(): Response<AnimeListResponse>



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
    val qualities: List<Quality>,
    val list: List<ListItem>// List of available qualities
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

data class AnimeData(
    val title: String,
    val samehadakuUrl: String,
    val poster: String,
    val episodes: String,
    val releasedOn: String,
    val animeId: String,
    val href: String,
    val startWith: String,
    val list: List<AnimeCategory>
)
data class AnimeItem(
    val title: String,
    val animeId: String?,
    val href: String?,
    val samehadakuUrl: String?
)
data class AnimeResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: AnimeData
)
data class AnimeListItem(
    val title: String,
    val animeId: String,
    val href: String,
    val samehadakuUrl: String
)

data class AnimeListResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: AnimeListData
)

data class AnimeListData(
    val list: List<AnimeGroup>
)

data class AnimeGroup(
    val startWith: String,
    val animeList: List<Anime>
)
