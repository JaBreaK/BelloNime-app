package app.bellonime.jabreak.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.bellonime.jabreak.R
import app.bellonime.jabreak.adapter.ScheduleDayAdapter
import app.bellonime.jabreak.model.ScheduleResponse
import app.bellonime.jabreak.network.RetrofitInstance
import app.bellonime.jabreak.ui.detail.AnimeDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var recyclerViewSchedule: RecyclerView
    private lateinit var swipeRefreshDashboard: SwipeRefreshLayout
    private lateinit var scheduleDayAdapter: ScheduleDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerViewSchedule = view.findViewById(R.id.recyclerViewSchedule)
        swipeRefreshDashboard = view.findViewById(R.id.swipeRefreshDashboard)

        recyclerViewSchedule.layoutManager = LinearLayoutManager(requireContext())

        fetchScheduleData()

        swipeRefreshDashboard.setOnRefreshListener {
            fetchScheduleData()
        }

        return view
    }

    private fun fetchScheduleData() {
        RetrofitInstance.api.getSchedule().enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(
                call: Call<ScheduleResponse>,
                response: Response<ScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    if (scheduleResponse != null && scheduleResponse.ok) {
                        val days = scheduleResponse.data.days
                        if (!::scheduleDayAdapter.isInitialized) {
                            scheduleDayAdapter = ScheduleDayAdapter(days) { anime ->
                                // Tangani klik pada anime jadwal
                                val intent = Intent(requireContext(), AnimeDetailActivity::class.java)
                                intent.putExtra("animeId", anime.animeId)
                                startActivity(intent)
                            }
                            recyclerViewSchedule.adapter = scheduleDayAdapter
                        } else {
                            scheduleDayAdapter.updateData(days)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat jadwal", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
                swipeRefreshDashboard.isRefreshing = false
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                swipeRefreshDashboard.isRefreshing = false
            }
        })
    }
}
