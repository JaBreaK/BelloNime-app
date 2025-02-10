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
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.AnimeDetail
import app.bellonime.jabreak.network.AnimeDetailResponse
import app.bellonime.jabreak.network.RetrofitInstance
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jp.wasabeef.glide.transformations.BlurTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var backgroundImage: ImageView
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
    private lateinit var connections: TextView
    // Perbaiki tipe dari ChipGroup ke FlexboxLayout
    private lateinit var episodesList: FlexboxLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        // Inisialisasi view, termasuk backgroundImage
        backgroundImage = findViewById(R.id.backgroundImage)
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

        // Contoh static untuk genre (bisa dihapus atau disesuaikan)
        genres.text = "ini loding"

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
        // Load poster biasa ke ImageView poster
        Glide.with(this)
            .load(animeDetail.poster)
            .into(poster)

        // Load poster yang sama dengan efek blur ke backgroundImage
        Glide.with(this)
            .load(animeDetail.poster)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(backgroundImage)

        // Bind data ke view lainnya
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

        // Handle episodeList menggunakan ChipGroup
        val episodeList = animeDetail.episodeList
        episodesList.removeAllViews() // Pastikan ChipGroup kosong

        if (episodeList.isNullOrEmpty()) {
            val chip = Chip(this)
            chip.text = "No episodes available"
            chip.isClickable = false
            chip.isCheckable = false
            chip.layoutParams = ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.MATCH_PARENT,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
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
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt()
            )
            episodesList.addView(chip)
        } else {
            for ((index, episode) in episodeList.withIndex()) {
                val chip = Chip(this)
                chip.text = "Ep ${episode.title ?: index + 1}"

                // Atur bentuk Chip (sudut melengkung)
                chip.shapeAppearanceModel = chip.shapeAppearanceModel
                    .toBuilder()
                    .setAllCornerSizes(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics))
                    .build()

                // Atur ukuran dan margin agar jadi grid 3xN
                val layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
                ).apply {
                    flexGrow = 1f  // Semua item mendapatkan ukuran yang sama
                    flexBasisPercent = 1f / 4f  // Setiap Chip mengisi 1/3 lebar layar
                    val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
                    setMargins(margin, margin, margin, margin)
                }
                chip.layoutParams = layoutParams

                // Warna latar belakang Chip
                chip.setChipBackgroundColorResource(R.color.eps)
                chip.setTextColor(Color.WHITE)

                chip.isCheckable = false
                chip.isClickable = true

                // Event klik Chip
                chip.setOnClickListener {
                    val episodeId = episode.episodeId ?: "default_episode"
                    val intent = Intent(this, app.bellonime.jabreak.ui.episode.EpisodeDetailActivity::class.java)
                    intent.putExtra("episodeId", episodeId)
                    startActivity(intent)
                }

                // Tambahkan Chip ke FlexboxLayout
                episodesList.addView(chip)
            }


        }

        // Handle connections
        connections.text = "Connections: ${animeDetail.synopsis?.connections?.joinToString(", ") { it.title ?: "" } ?: "No connections available"}"
    }
}
