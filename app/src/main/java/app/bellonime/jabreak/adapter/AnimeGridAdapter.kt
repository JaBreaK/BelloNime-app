package app.bellonime.jabreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.Anime
import com.bumptech.glide.Glide

class AnimeGridAdapter(
    private var animeList: List<Anime>,
    private val onItemClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeGridAdapter.AnimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.bind(anime, onItemClick)
    }

    override fun getItemCount(): Int = animeList.size

    fun updateData(newAnimeList: List<Anime>) {
        animeList = newAnimeList
        notifyDataSetChanged()
    }

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.animeTitle)
        private val poster: ImageView = view.findViewById(R.id.animePoster)
        private val episodes: TextView = view.findViewById(R.id.animeEpisodes)

        fun bind(anime: Anime, onItemClick: (Anime) -> Unit) {
            title.text = anime.title
            episodes.text = "Ep ${anime.episodes}"
            Glide.with(poster.context)
                .load(anime.poster)
                .into(poster)

            itemView.setOnClickListener {
                onItemClick(anime)
            }
        }
    }
}
