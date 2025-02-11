package app.bellonime.jabreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.ScheduleAnime
import app.bellonime.jabreak.model.ScheduleDay

class ScheduleDayAdapter(
    private var dayList: List<ScheduleDay>,
    private val onAnimeClick: (ScheduleAnime) -> Unit
) : RecyclerView.Adapter<ScheduleDayAdapter.ScheduleDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_day, parent, false)
        return ScheduleDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleDayViewHolder, position: Int) {
        val scheduleDay = dayList[position]
        holder.bind(scheduleDay, onAnimeClick)
    }

    override fun getItemCount(): Int = dayList.size

    fun updateData(newDayList: List<ScheduleDay>) {
        dayList = newDayList
        notifyDataSetChanged()
    }

    class ScheduleDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textDay: TextView = view.findViewById(R.id.textDay)
        private val recyclerViewAnimeList: RecyclerView = view.findViewById(R.id.recyclerViewAnimeList)

        fun bind(scheduleDay: ScheduleDay, onAnimeClick: (ScheduleAnime) -> Unit) {
            textDay.text = scheduleDay.day

            // Set layout manager horizontal untuk nested RecyclerView
            recyclerViewAnimeList.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            val animeAdapter = ScheduleAnimeAdapter(scheduleDay.animeList, onAnimeClick)
            recyclerViewAnimeList.adapter = animeAdapter
        }
    }
}
