package app.bellonime.jabreak.model

data class Anime(
    val title: String,
    val poster: String,
    val episodes: String,
    val releasedOn: String,
    val animeId: String,
    val href: String,
    val startWith: String,
    val samehadakuUrl: String,
)
data class AnimeData(
    val list: List<AnimeCategory>
)
