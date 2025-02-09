package app.bellonime.jabreak.ui.episode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.EpisodeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import app.bellonime.jabreak.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var watchNowButton: TextView
    private lateinit var prevEpisodeButton: TextView
    private lateinit var nextEpisodeButton: TextView
    private lateinit var serverChipGroup: ChipGroup

    // Variabel untuk menyimpan URL streaming yang aktif
    private var currentStreamingUrl: String? = null

    // Base URL (jika URL yang diterima bersifat relative)
    private val baseUrl = "https://bellonime.vercel.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Inisialisasi view
        titleText = findViewById(R.id.episodeTitle)
        watchNowButton = findViewById(R.id.watchNowButton)
        prevEpisodeButton = findViewById(R.id.prevEpisodeButton)
        nextEpisodeButton = findViewById(R.id.nextEpisodeButton)
        serverChipGroup = findViewById(R.id.serverChipGroup)

        // Ambil episodeId dari intent
        val episodeId = intent.getStringExtra("episodeId")
        if (episodeId.isNullOrEmpty()) {
            Toast.makeText(this, "Episode ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Panggil API untuk mendapatkan detail episode
        fetchEpisodeDetail(episodeId)
    }

    private fun fetchEpisodeDetail(episodeId: String) {
        RetrofitInstance.api.getEpisodeDetail(episodeId).enqueue(object : Callback<EpisodeDetailResponse> {
            override fun onResponse(call: Call<EpisodeDetailResponse>, response: Response<EpisodeDetailResponse>) {
                if (response.isSuccessful) {
                    val episode = response.body()?.data
                    if (episode != null) {
                        bindEpisodeDetail(episode)
                    } else {
                        Toast.makeText(this@EpisodeDetailActivity, "Episode detail is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EpisodeDetailActivity, "Failed to load episode detail", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<EpisodeDetailResponse>, t: Throwable) {
                Toast.makeText(this@EpisodeDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindEpisodeDetail(episode: EpisodeDetail) {
        // Tampilkan judul episode
        titleText.text = episode.title ?: "Untitled"

        // Set default streaming URL sebagai awal
        currentStreamingUrl = episode.defaultStreamingUrl

        // Watch Now: Buka URL streaming yang aktif saat tombol diklik
        watchNowButton.setOnClickListener {
            currentStreamingUrl?.let { url ->
                val finalUrl = if (url.startsWith("http")) url else baseUrl + url
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Streaming URL not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Konfigurasi tombol Prev Episode
        if (episode.hasPrevEpisode && episode.prevEpisode != null && !episode.prevEpisode.episodeId.isNullOrEmpty()) {
            prevEpisodeButton.visibility = View.VISIBLE
            prevEpisodeButton.setOnClickListener {
                val intent = Intent(this, EpisodeDetailActivity::class.java)
                intent.putExtra("episodeId", episode.prevEpisode.episodeId)
                startActivity(intent)
                finish()
            }
        } else {
            prevEpisodeButton.visibility = View.GONE
        }

        // Konfigurasi tombol Next Episode
        if (episode.hasNextEpisode && episode.nextEpisode != null && !episode.nextEpisode.episodeId.isNullOrEmpty()) {
            nextEpisodeButton.visibility = View.VISIBLE
            nextEpisodeButton.setOnClickListener {
                val intent = Intent(this, EpisodeDetailActivity::class.java)
                intent.putExtra("episodeId", episode.nextEpisode.episodeId)
                startActivity(intent)
                finish()
            }
        } else {
            nextEpisodeButton.visibility = View.GONE
        }

        // Tampilkan pilihan server berdasarkan kualitas yang tersedia
        serverChipGroup.removeAllViews()
        val qualities = episode.server?.qualities
        if (qualities != null) {
            for (quality in qualities) {
                // Pastikan ada setidaknya satu server dalam serverList
                if (!quality.serverList.isNullOrEmpty()) {
                    val chip = Chip(this)
                    chip.text = quality.title ?: "Unknown"
                    chip.isClickable = true
                    chip.isCheckable = false
                    chip.setOnClickListener {
                        // Saat chip diklik, gunakan server pertama dari daftar untuk kualitas ini
                        val serverItem = quality.serverList.firstOrNull()
                        if (serverItem != null) {
                            var newUrl = serverItem.href ?: ""
                            if (!newUrl.startsWith("http")) {
                                newUrl = baseUrl + newUrl
                            }
                            currentStreamingUrl = newUrl
                            Toast.makeText(this, "Switched to ${quality.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    serverChipGroup.addView(chip)
                }
            }
        }
    }
}
