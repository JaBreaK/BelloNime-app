package app.bellonime.jabreak.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.bellonime.jabreak.R
import app.bellonime.jabreak.adapter.AnimeCarouselAdapter
import app.bellonime.jabreak.adapter.AnimeGridAdapter
import app.bellonime.jabreak.network.ApiResponse
import app.bellonime.jabreak.network.RetrofitInstance
import app.bellonime.jabreak.ui.detail.AnimeDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var carouselAdapter: AnimeCarouselAdapter
    private lateinit var gridAdapter: AnimeGridAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    // Untuk auto-scroll carousel setiap 5 detik
    private var currentCarouselPosition = 0
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (::carouselAdapter.isInitialized && carouselAdapter.itemCount > 0) {
                currentCarouselPosition = (currentCarouselPosition + 1) % carouselAdapter.itemCount
                smoothScrollToPosition(currentCarouselPosition)
            }
            autoScrollHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        carouselRecyclerView = view.findViewById(R.id.recyclerViewCarousel)
        gridRecyclerView = view.findViewById(R.id.recyclerViewGrid)

        // Setup RecyclerView Carousel (Horizontal, full-width)
        carouselRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        // Gunakan PagerSnapHelper agar tiap item “snap” layaknya halaman penuh
        PagerSnapHelper().attachToRecyclerView(carouselRecyclerView)
        // Update posisi saat user scroll manual
        carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = carouselRecyclerView.layoutManager as LinearLayoutManager
                    currentCarouselPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                }
            }
        })

        // Setup RecyclerView Grid (Grid layout 2 kolom)
        gridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        fetchAnimeData()

        swipeRefresh.setOnRefreshListener {
            fetchAnimeData()
        }

        return view
    }

    private fun smoothScrollToPosition(position: Int) {
        // Custom LinearSmoothScroller untuk transisi smooth
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                // Semakin kecil nilainya, semakin smooth (lambat) transisinya
                return 50f / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        carouselRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun fetchAnimeData() {
        RetrofitInstance.api.getRecentAnime().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val animeList = response.body()?.data?.recent?.animeList ?: emptyList()

                    // Untuk carousel, ambil 5 item pertama (jika ada)
                    val carouselList = if (animeList.size >= 5) animeList.take(5) else animeList

                    // Inisialisasi atau perbarui adapter Carousel
                    if (!::carouselAdapter.isInitialized) {
                        carouselAdapter = AnimeCarouselAdapter(carouselList) { anime ->
                            val intent = Intent(requireContext(), AnimeDetailActivity::class.java)
                            intent.putExtra("animeId", anime.animeId)
                            startActivity(intent)
                        }
                        carouselRecyclerView.adapter = carouselAdapter
                    } else {
                        carouselAdapter.updateData(carouselList)
                    }

                    // Inisialisasi atau perbarui adapter Grid
                    if (!::gridAdapter.isInitialized) {
                        gridAdapter = AnimeGridAdapter(animeList) { anime ->
                            val intent = Intent(requireContext(), AnimeDetailActivity::class.java)
                            intent.putExtra("animeId", anime.animeId)
                            startActivity(intent)
                        }
                        gridRecyclerView.adapter = gridAdapter
                    } else {
                        gridAdapter.updateData(animeList)
                    }

                    swipeRefresh.isRefreshing = false
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                    swipeRefresh.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                swipeRefresh.isRefreshing = false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        autoScrollHandler.postDelayed(autoScrollRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }
}
