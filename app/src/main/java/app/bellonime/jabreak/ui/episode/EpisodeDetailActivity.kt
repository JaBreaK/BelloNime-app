package app.bellonime.jabreak.ui.episode

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.EpisodeDetail
import app.bellonime.jabreak.model.EpisodeDetailResponse
import app.bellonime.jabreak.model.Quality
import app.bellonime.jabreak.network.RetrofitInstance
import app.bellonime.jabreak.network.ServerResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var webView: WebView
    private lateinit var prevEpisodeButton: TextView
    private lateinit var nextEpisodeButton: TextView

    // Dropdown dan ChipGroup untuk pilihan kualitas dan server
    private lateinit var qualityDropdown: AutoCompleteTextView
    private lateinit var serverChipGroup: ChipGroup

    // Untuk tampilan video fullscreen (custom view)
    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var originalSystemUiVisibility: Int = 0
    private var originalOrientation: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Inisialisasi view
        titleText = findViewById(R.id.episodeTitle)
        webView = findViewById(R.id.webView)
        prevEpisodeButton = findViewById(R.id.prevEpisodeButton)
        nextEpisodeButton = findViewById(R.id.nextEpisodeButton)
        qualityDropdown = findViewById(R.id.qualityDropdown)
        serverChipGroup = findViewById(R.id.serverChipGroup)

        setupWebView()

        // Ambil episodeId dari Intent
        val episodeId = intent.getStringExtra("episodeId")
        if (episodeId.isNullOrEmpty()) {
            Toast.makeText(this, "Episode ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        fetchEpisodeDetail(episodeId)
    }

    private fun setupWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
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
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                showCustomView(view, callback)
            }
            override fun onHideCustomView() {
                hideCustomView()
            }
        }
    }

    private fun showCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }
        originalSystemUiVisibility = window.decorView.systemUiVisibility
        originalOrientation = requestedOrientation
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        customView = view
        customViewCallback = callback
        val decor = window.decorView as LinearLayout
        decor.addView(customView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))
        // Sembunyikan UI utama
        titleText.visibility = View.GONE
        prevEpisodeButton.visibility = View.GONE
        nextEpisodeButton.visibility = View.GONE
        qualityDropdown.visibility = View.GONE
        serverChipGroup.visibility = View.GONE
    }

    private fun hideCustomView() {
        if (customView == null) return
        window.decorView.systemUiVisibility = originalSystemUiVisibility
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        val decor = window.decorView as LinearLayout
        decor.removeView(customView)
        customView = null
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        // Tampilkan kembali UI utama
        titleText.visibility = View.VISIBLE
        prevEpisodeButton.visibility = View.VISIBLE
        nextEpisodeButton.visibility = View.VISIBLE
        qualityDropdown.visibility = View.VISIBLE
        serverChipGroup.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        when {
            customView != null -> hideCustomView()
            webView.canGoBack() -> webView.goBack()
            else -> super.onBackPressed()
        }
    }

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

    private fun bindEpisodeDetail(episode: EpisodeDetail) {
        titleText.text = episode.title ?: "Untitled Episode"
        episode.defaultStreamingUrl?.let { loadVideoUrl(it) }
        setupNavigationButtons(episode)
        // Jika data server tersedia, atur dropdown kualitas
        episode.server?.qualities?.let { qualities ->
            setupQualityDropdown(qualities)
        }
    }

    private fun loadVideoUrl(url: String) {
        webView.loadUrl(url)
    }

    private fun setupNavigationButtons(episode: EpisodeDetail) {
        episode.prevEpisode?.episodeId?.let { id ->
            prevEpisodeButton.visibility = View.VISIBLE
            prevEpisodeButton.setOnClickListener { restartActivityWithEpisode(id) }
        } ?: run { prevEpisodeButton.visibility = View.GONE }
        episode.nextEpisode?.episodeId?.let { id ->
            nextEpisodeButton.visibility = View.VISIBLE
            nextEpisodeButton.setOnClickListener { restartActivityWithEpisode(id) }
        } ?: run { nextEpisodeButton.visibility = View.GONE }
    }

    /**
     * Atur dropdown kualitas menggunakan AutoCompleteTextView.
     * Daftar kualitas akan diisi berdasarkan data API (misalnya "360p", "480p", "720p", "1080p", "4K").
     * Saat dipilih, daftar server untuk kualitas tersebut akan diperbarui.
     */
    private fun setupQualityDropdown(qualities: List<Quality>) {
        // Ambil daftar judul kualitas
        val qualityTitles = qualities.map { it.title ?: "Unknown" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, qualityTitles)
        qualityDropdown.setAdapter(adapter)

        // Jika tersedia, pilih kualitas pertama secara default
        if (qualityTitles.isNotEmpty()) {
            qualityDropdown.setText(qualityTitles[0], false)
            updateServerChipsForQuality(qualities[0])
        }

        qualityDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selectedQualityTitle = parent.getItemAtPosition(position) as String
            val selectedQuality = qualities.find { it.title == selectedQualityTitle }
            if (selectedQuality != null) {
                updateServerChipsForQuality(selectedQuality)
            }
        }
    }

    /**
     * Perbarui ChipGroup dengan daftar server berdasarkan kualitas yang dipilih.
     */
    private fun updateServerChipsForQuality(quality: Quality) {
        serverChipGroup.removeAllViews()
        quality.serverList?.forEach { server ->
            if (!server.serverId.isNullOrEmpty()) {
                val chip = Chip(this).apply {
                    text = server.title ?: "Unknown Server"
                    isClickable = true
                    isCheckable = false
                    setOnClickListener { fetchVideoUrl(server.serverId) }
                }
                serverChipGroup.addView(chip)
            }
        }
    }

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

    private fun restartActivityWithEpisode(episodeId: String) {
        Intent(this, EpisodeDetailActivity::class.java).apply {
            putExtra("episodeId", episodeId)
            startActivity(this)
        }
        finish()
    }
}
