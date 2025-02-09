package app.bellonime.jabreak.model

data class AnimeDetail(
    val title: String,
    val poster: String,
    val score: Score?,
    val japanese: String?,
    val synonyms: String?,
    val english: String?,
    val status: String?,
    val type: String?,
    val source: String?,
    val duration: String?,
    val episodes: Int?,
    val season: String?,
    val studios: String?,
    val producers: String?,
    val aired: String?,
    val trailer: String?,
    val synopsis: Synopsis?,
    val genreList: List<Genre>?,
    val batchList: List<Batch>?,
    val episodeList: List<Episode>?
)

data class Score(
    val value: String?,
    val users: String?
)

data class Synopsis(
    val paragraphs: List<String>?,
    val connections: List<Connection>?
)

data class Connection(
    val title: String?,
    val animeId: String?,
    val href: String?,
    val samehadakuUrl: String?
)

data class Genre(
    val title: String?,
    val genreId: String?,
    val href: String?,
    val samehadakuUrl: String?
)

data class Batch(
    val title: String?,
    val batchId: String?,
    val href: String?,
    val samehadakuUrl: String?
)

data class Episode(
    val title: Int?,
    val episodeId: String?,
    val href: String?,
    val number: String?,
    val samehadakuUrl: String?
)

