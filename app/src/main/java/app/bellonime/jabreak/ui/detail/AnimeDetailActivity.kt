package app.bellonime.jabreak.ui.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.AnimeDetail
import app.bellonime.jabreak.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import app.bellonime.jabreak.network.AnimeDetailResponse

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var score: TextView
    private lateinit var japanese: TextView
    private lateinit var synonyms: TextView
    private lateinit var english: TextView
    private lateinit var status: TextView
    private lateinit var type: TextView
    private lateinit var source: TextView
    private lateinit var duration: TextView
    private lateinit var episodes: TextView
    private lateinit var season: TextView
    private lateinit var studios: TextView
    private lateinit var producers: TextView
    private lateinit var aired: TextView
    private lateinit var synopsis: TextView
    private lateinit var genres: TextView
    private lateinit var batches: TextView
    // Ubah tipe dari TextView ke ChipGroup
    private lateinit var episodesList: ChipGroup
    private lateinit var connections: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        // Inisialisasi view
        poster = findViewById(R.id.animePoster)
        title = findViewById(R.id.animeTitle)
        score = findViewById(R.id.animeScore)
        japanese = findViewById(R.id.animeJapanese)
        synonyms = findViewById(R.id.animeSynonyms)
        english = findViewById(R.id.animeEnglish)
        status = findViewById(R.id.animeStatus)
        type = findViewById(R.id.animeType)
        source = findViewById(R.id.animeSource)
        duration = findViewById(R.id.animeDuration)
        episodes = findViewById(R.id.animeEpisodes)
        season = findViewById(R.id.animeSeason)
        studios = findViewById(R.id.animeStudios)
        producers = findViewById(R.id.animeProducers)
        aired = findViewById(R.id.animeAired)
        synopsis = findViewById(R.id.animeSynopsis)
        genres = findViewById(R.id.animeGenres)
        batches = findViewById(R.id.animeBatches)
        episodesList = findViewById(R.id.animeEpisodesList)
        connections = findViewById(R.id.animeConnections)

        // Ambil animeId dari intent
        val animeId = intent.getStringExtra("animeId") ?: run {
            Toast.makeText(this, "Anime ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val genres: TextView = findViewById(R.id.animeGenres)
        genres.text = "Comedy  Romance  Supernatural"
        // Panggil API untuk detail anime
        fetchAnimeDetail(animeId)
    }

    private fun fetchAnimeDetail(animeId: String) {
        RetrofitInstance.api.getAnimeDetail(animeId).enqueue(object : Callback<AnimeDetailResponse> {
            override fun onResponse(call: Call<AnimeDetailResponse>, response: Response<AnimeDetailResponse>) {
                if (response.isSuccessful) {
                    val animeDetail = response.body()?.data
                    if (animeDetail != null) {
                        bindAnimeDetail(animeDetail)
                    } else {
                        Toast.makeText(this@AnimeDetailActivity, "Anime detail is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AnimeDetailActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AnimeDetailResponse>, t: Throwable) {
                Toast.makeText(this@AnimeDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindAnimeDetail(animeDetail: AnimeDetail) {
        // Bind data ke view lainnya
        Glide.with(this).load(animeDetail.poster).into(poster)
        title.text = animeDetail.title ?: "N/A"
        score.text = "${animeDetail.score?.value ?: "N/A"} (${animeDetail.score?.users ?: "N/A"} User)"
        japanese.text = "Japanese: ${animeDetail.japanese ?: "N/A"}"
        synonyms.text = "Synonyms: ${animeDetail.synonyms ?: "N/A"}"
        english.text = "English: ${animeDetail.english ?: "N/A"}"
        status.text = "${animeDetail.status ?: "N/A"}"
        type.text = "${animeDetail.type ?: "N/A"}"
        source.text = "Source: ${animeDetail.source ?: "N/A"}"
        duration.text = "Duration: ${animeDetail.duration ?: "N/A"}"
        episodes.text = "Episodes: ${animeDetail.episodes ?: "N/A"}"
        season.text = "Season: ${animeDetail.season ?: "N/A"}"
        studios.text = "Studios: ${animeDetail.studios ?: "N/A"}"
        producers.text = "Producers: ${animeDetail.producers ?: "N/A"}"
        aired.text = "Aired: ${animeDetail.aired ?: "N/A"}"
        synopsis.text = animeDetail.synopsis?.paragraphs?.joinToString("\n\n") ?: "No synopsis available"

        // Handle genreList
        genres.text = "${animeDetail.genreList?.joinToString(", ") { it.title ?: "" } ?: "No genres available"}"

        // Handle batchList
        batches.text = "Batches: ${animeDetail.batchList?.joinToString(", ") { it.title ?: "" } ?: "No batches available"}"

        // Handle episodeList menggunakan ChipGroup dengan tampilan seperti tombol
        val episodeList = animeDetail.episodeList
        episodesList.removeAllViews() // Pastikan ChipGroup kosong

        if (episodeList.isNullOrEmpty()) {
            val chip = Chip(this)
            chip.text = "No episodes available"
            chip.isClickable = false
            chip.isCheckable = false
            chip.layoutParams = ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.MATCH_PARENT,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
            ).apply {
                val marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                val marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
                val marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                setMargins(0, marginTop, marginEnd, marginBottom)
            }
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            chip.setTextColor(Color.WHITE)
            chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
            chip.setBackgroundResource(R.drawable.episode_button_background)
            chip.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
            )
            episodesList.addView(chip)
        } else {
            for (episode in episodeList) {
                val chip = Chip(this)
                chip.text = "Episode ${episode.title ?: "Unknown Episode"}"
                chip.layoutParams = ChipGroup.LayoutParams(
                    ChipGroup.LayoutParams.MATCH_PARENT,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
                ).apply {
                    val marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                    val marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
                    val marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                    setMargins(0, marginTop, marginEnd, marginBottom)
                }
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                chip.setTextColor(Color.WHITE)
                chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
                chip.setBackgroundResource(R.drawable.episode_button_background)
                chip.setPadding(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
                )
                chip.isClickable = true
                chip.isCheckable = false
                chip.setOnClickListener {
                    // Tambahkan kode untuk membuka EpisodeDetailActivity
                    // Jika episode.episodeId tidak null, gunakan nilainya; jika null, gunakan nilai default
                    val episodeId = episode.episodeId ?: "one-piece-gyojin-tou-hen-episode-15"
                    val intent = Intent(this, app.bellonime.jabreak.ui.episode.EpisodeDetailActivity::class.java)
                    intent.putExtra("episodeId", episodeId)
                    startActivity(intent)
                }
                episodesList.addView(chip)
            }
        }

        // Handle connections
        connections.text = "Connections: ${animeDetail.synopsis?.connections?.joinToString(", ") { it.title ?: "" } ?: "No connections available"}"
    }
}
