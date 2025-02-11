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

class AnimeCarouselAdapter(
    private var animeList: List<Anime>,
    private val onItemClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeCarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val anime = animeList[position]
        holder.bind(anime, onItemClick)
    }

    override fun getItemCount(): Int = animeList.size

    fun updateData(newAnimeList: List<Anime>) {
        animeList = newAnimeList
        notifyDataSetChanged()
    }

    class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.carouselTitle)
        private val poster: ImageView = view.findViewById(R.id.carouselPoster)
        private val episodes: TextView = view.findViewById(R.id.carouselEpisodes)

        fun bind(anime: Anime, onItemClick: (Anime) -> Unit) {
            title.text = anime.title
            episodes.text = "Episode: ${anime.episodes}"
            Glide.with(poster.context)
                .load(anime.poster)
                .into(poster)

            itemView.setOnClickListener { onItemClick(anime) }
        }
    }
}
