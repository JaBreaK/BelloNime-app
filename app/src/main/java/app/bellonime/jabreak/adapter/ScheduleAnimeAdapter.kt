package app.bellonime.jabreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.ScheduleAnime
import com.bumptech.glide.Glide

class ScheduleAnimeAdapter(
    private var animeList: List<ScheduleAnime>,
    private val onItemClick: (ScheduleAnime) -> Unit
) : RecyclerView.Adapter<ScheduleAnimeAdapter.ScheduleAnimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_anime, parent, false)
        return ScheduleAnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleAnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.bind(anime, onItemClick)
    }

    override fun getItemCount(): Int = animeList.size

    fun updateData(newAnimeList: List<ScheduleAnime>) {
        animeList = newAnimeList
        notifyDataSetChanged()
    }

    class ScheduleAnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imagePoster: ImageView = view.findViewById(R.id.imagePoster)
        private val textTitle: TextView = view.findViewById(R.id.textTitle)
        private val textTypeScore: TextView = view.findViewById(R.id.textTypeScore)

        fun bind(anime: ScheduleAnime, onItemClick: (ScheduleAnime) -> Unit) {
            textTitle.text = anime.title
            textTypeScore.text = "${anime.type} | Score: ${anime.score}"
            Glide.with(itemView.context)
                .load(anime.poster)
                .into(imagePoster)

            itemView.setOnClickListener { onItemClick(anime) }
        }
    }
}
