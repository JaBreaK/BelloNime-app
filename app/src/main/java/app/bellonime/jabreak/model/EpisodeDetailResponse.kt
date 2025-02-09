package app.bellonime.jabreak.model

data class EpisodeDetailResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: EpisodeDetail
)

data class EpisodeDetail(
    val title: String?,
    val defaultStreamingUrl: String?,
    val hasPrevEpisode: Boolean,
    val prevEpisode: EpisodeRef?,
    val hasNextEpisode: Boolean,
    val nextEpisode: EpisodeRef?,
    val server: Server?
)

data class EpisodeRef(
    val title: String?,
    val episodeId: String?,
    val href: String?,
    val samehadakuUrl: String?
)

data class Server(
    val qualities: List<Quality>?
)

data class Quality(
    val title: String?,
    val serverList: List<ServerItem>?
)

data class ServerItem(
    val title: String?,
    val serverId: String?,
    val href: String?
)



// EpisodeNavigation.kt
data class EpisodeNavigation(
    val episodeId: String?
)


