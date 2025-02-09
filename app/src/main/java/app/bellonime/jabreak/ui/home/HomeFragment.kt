package app.bellonime.jabreak.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.adapter.AnimeAdapter
import app.bellonime.jabreak.network.ApiResponse
import app.bellonime.jabreak.network.RetrofitInstance
import app.bellonime.jabreak.ui.detail.AnimeDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnimeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 3 else 1 // Header 1 baris penuh, item anime tetap 3 kolom
            }
        }

        recyclerView.layoutManager = layoutManager
        fetchAnimeData()

        return view
    }



    private fun fetchAnimeData() {
        RetrofitInstance.api.getRecentAnime().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val animeList = response.body()?.data?.recent?.animeList ?: emptyList()
                    adapter = AnimeAdapter(animeList) { anime ->
                        val intent = Intent(requireContext(), AnimeDetailActivity::class.java)
                        intent.putExtra("animeId", anime.animeId)
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}