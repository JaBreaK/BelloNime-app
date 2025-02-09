package app.bellonime.jabreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.bellonime.jabreak.R
import app.bellonime.jabreak.network.ServerItem

class ServerAdapter(
    private val serverList: List<ServerItem>,
    private val listener: OnServerClickListener
) : RecyclerView.Adapter<ServerAdapter.ServerViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    interface OnServerClickListener {
        fun onServerClick(server: ServerItem, position: Int)
    }

    inner class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val qualityText: TextView = itemView.findViewById(R.id.qualityText)
        val serverTitleText: TextView = itemView.findViewById(R.id.serverTitleText)
        val serverCard: CardView = itemView.findViewById(R.id.serverCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = serverList[position]
        holder.qualityText.text = server.quality
        holder.serverTitleText.text = server.serverTitle

        // Highlight item yang dipilih
        if (position == selectedPosition) {
            holder.serverCard.setCardBackgroundColor(holder.itemView.context.getColor(R.color.purple_200))
        } else {
            holder.serverCard.setCardBackgroundColor(holder.itemView.context.getColor(android.R.color.transparent))
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            listener.onServerClick(server, selectedPosition)
        }
    }

    override fun getItemCount(): Int = serverList.size
}
