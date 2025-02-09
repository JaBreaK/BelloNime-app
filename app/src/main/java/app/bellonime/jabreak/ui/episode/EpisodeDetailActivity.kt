package app.bellonime.jabreak.ui.episode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.adapter.ServerAdapter
import app.bellonime.jabreak.model.EpisodeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import app.bellonime.jabreak.model.Quality
import app.bellonime.jabreak.network.RetrofitInstance
import app.bellonime.jabreak.network.ServerItem
import app.bellonime.jabreak.network.ServerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var webView: WebView
    private lateinit var prevEpisodeButton: TextView
    private lateinit var nextEpisodeButton: TextView
    private lateinit var serverRecyclerView: RecyclerView

    // URL base (jika diperlukan)
    private val baseUrl = "https://bellonime.vercel.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Inisialisasi view dari XML
        titleText = findViewById(R.id.episodeTitle)
        webView = findViewById(R.id.webView)
        prevEpisodeButton = findViewById(R.id.prevEpisodeButton)
        nextEpisodeButton = findViewById(R.id.nextEpisodeButton)
        serverRecyclerView = findViewById(R.id.serverRecyclerView)

        // Setup RecyclerView (horizontal)
        serverRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Konfigurasi WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(this@EpisodeDetailActivity, "Error loading video: $description", Toast.LENGTH_SHORT).show()
            }
        }
        webView.webChromeClient = WebChromeClient()

        // Ambil episodeId dari Intent
        val episodeId = intent.getStringExtra("episodeId")
        if (episodeId.isNullOrEmpty()) {
            Toast.makeText(this, "Episode ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        fetchEpisodeDetail(episodeId)
    }

    // Fungsi untuk mengambil detail episode dari API
    private fun fetchEpisodeDetail(episodeId: String) {
        RetrofitInstance.api.getEpisodeDetail(episodeId).enqueue(object : Callback<EpisodeDetailResponse> {
            override fun onResponse(call: Call<EpisodeDetailResponse>, response: Response<EpisodeDetailResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { bindEpisodeDetail(it) } ?: run {
                        Toast.makeText(this@EpisodeDetailActivity, "Invalid episode data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EpisodeDetailActivity, "Failed to load episode", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EpisodeDetailResponse>, t: Throwable) {
                Toast.makeText(this@EpisodeDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk mengikat data episode ke tampilan
    private fun bindEpisodeDetail(episode: EpisodeDetail) {
        titleText.text = episode.title ?: "Untitled Episode"

        // Muat URL default (contoh: preview video)
        episode.defaultStreamingUrl?.let { loadVideoUrl(it) }

        // Setup navigasi episode (prev/next)
        setupNavigationButtons(episode)

        // Tampilkan pilihan server menggunakan RecyclerView
        episode.server?.qualities?.let { qualities ->
            setupServerRecyclerView(qualities)
        }
    }

    // Fungsi untuk memuat URL video ke WebView
    private fun loadVideoUrl(url: String) {
        webView.loadUrl(url)
    }

    // Fungsi untuk navigasi ke episode sebelumnya/selanjutnya
    private fun setupNavigationButtons(episode: EpisodeDetail) {
        episode.prevEpisode?.episodeId?.let { id ->
            prevEpisodeButton.visibility = View.VISIBLE
            prevEpisodeButton.setOnClickListener {
                restartActivityWithEpisode(id)
            }
        } ?: run { prevEpisodeButton.visibility = View.GONE }

        episode.nextEpisode?.episodeId?.let { id ->
            nextEpisodeButton.visibility = View.VISIBLE
            nextEpisodeButton.setOnClickListener {
                restartActivityWithEpisode(id)
            }
        } ?: run { nextEpisodeButton.visibility = View.GONE }
    }

    // Fungsi untuk setup RecyclerView dengan pilihan server dari setiap kualitas
    private fun setupServerRecyclerView(qualities: List<Quality>) {
        val serverItems = mutableListOf<ServerItem>()
        qualities.forEach { quality ->
            quality.serverList?.forEach { server ->
                // Pastikan serverId tidak kosong
                if (!server.serverId.isNullOrEmpty()) {
                    serverItems.add(
                        ServerItem(
                            quality = quality.title ?: "Unknown Quality",
                            serverTitle = server.title ?: "Unknown Server",
                            serverId = server.serverId
                        )
                    )
                }
            }
        }
        Log.d("EpisodeDetail", "Number of server items: ${serverItems.size}")
        if (serverItems.isEmpty()) {
            Toast.makeText(this, "No server options available", Toast.LENGTH_SHORT).show()
        }
        val adapter = ServerAdapter(serverItems, object : ServerAdapter.OnServerClickListener {
            override fun onServerClick(server: ServerItem, position: Int) {
                fetchVideoUrl(server.serverId)
            }
        })
        serverRecyclerView.adapter = adapter
    }

    // Fungsi untuk memanggil API getVideoUrl berdasarkan serverId
    private fun fetchVideoUrl(serverId: String) {
        RetrofitInstance.api.getVideoUrl(serverId).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                if (response.isSuccessful) {
                    val videoUrl = response.body()?.data?.url
                    if (!videoUrl.isNullOrEmpty()) {
                        loadVideoUrl(videoUrl)
                    } else {
                        Toast.makeText(this@EpisodeDetailActivity, "Video URL not available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EpisodeDetailActivity, "Failed to get video: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@EpisodeDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Restart activity untuk pindah episode
    private fun restartActivityWithEpisode(episodeId: String) {
        Intent(this, EpisodeDetailActivity::class.java).apply {
            putExtra("episodeId", episodeId)
            startActivity(this)
        }
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
