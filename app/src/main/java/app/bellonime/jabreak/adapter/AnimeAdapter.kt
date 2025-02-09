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

class AnimeAdapter(
    private val animeList: List<Anime>,
    private val onItemClick: (Anime) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime, parent, false)
            AnimeViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return animeList.size + 1 // +1 karena ada header
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind()
        } else if (holder is AnimeViewHolder) {
            val anime = animeList[position - 1] // Kurangi 1 karena posisi 0 adalah header
            holder.bind(anime, onItemClick)
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.headerTitle)

        fun bind() {
            title.text = "Anime Terbaru"
        }
    }

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.animeTitle)
        private val poster: ImageView = view.findViewById(R.id.animePoster)
        private val episodes: TextView = view.findViewById(R.id.animeEpisodes)

        fun bind(anime: Anime, onItemClick: (Anime) -> Unit) {
            title.text = anime.title
            episodes.text = " ${anime.episodes}"
            Glide.with(poster.context).load(anime.poster).into(poster)

            itemView.setOnClickListener {
                onItemClick(anime)
            }
        }
    }
}
