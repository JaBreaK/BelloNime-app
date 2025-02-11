package app.bellonime.jabreak.model

data class ScheduleResponse(
    val statusCode: Int,
    val statusMessage: String,
    val message: String,
    val ok: Boolean,
    val data: ScheduleData
)

data class ScheduleData(
    val days: List<ScheduleDay>
)

data class ScheduleDay(
    val day: String,
    val animeList: List<ScheduleAnime>
)

data class ScheduleAnime(
    val title: String,
    val poster: String,
    val type: String,
    val score: String,
    val estimation: String,
    val genres: String,
    val animeId: String,
    val href: String,
    val samehadakuUrl: String
)
