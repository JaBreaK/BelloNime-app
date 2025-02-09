package app.bellonime.jabreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import app.bellonime.jabreak.R
import app.bellonime.jabreak.model.Anime

class AnimeAdapter(
    private val animeList: List<Anime>,
    private val onItemClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.animeTitle)
        val poster: ImageView = view.findViewById(R.id.animePoster)
        val episodes: TextView = view.findViewById(R.id.animeEpisodes)
//        val releasedOn: TextView = view.findViewById(R.id.animeReleasedOn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.title.text = anime.title
        holder.episodes.text = " ${anime.episodes}"
//        holder.releasedOn.text = anime.releasedOn
        Glide.with(holder.poster.context).load(anime.poster).into(holder.poster)

        // Set onClickListener untuk item
        holder.itemView.setOnClickListener {
            onItemClick(anime)
        }
    }

    override fun getItemCount() = animeList.size
}