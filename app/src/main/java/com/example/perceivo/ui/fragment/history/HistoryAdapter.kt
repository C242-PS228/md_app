package com.example.perceivo.ui.fragment.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perceivo.R
import com.example.perceivo.model.SentimentData

class HistoryAdapter(
    private var items: List<SentimentData>,
    private val onItemClick: (SentimentData) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        private val sentimentTextView: TextView = itemView.findViewById(R.id.tv_sentiment_id)
        private val imgIcon: ImageView = itemView.findViewById(R.id.img_icon_app)

        fun bind(item: SentimentData) {
            titleTextView.text = item.title ?: "No Title"
            sentimentTextView.text = item.unique_id

            // Change Icon based on platform
            val platformIcon = when (item.platform) {
                "instagram" -> R.drawable.ic_instagram
                "tiktok" -> R.drawable.ic_tiktok
                "googlemaps" -> R.drawable.ic_gmaps
                else -> R.drawable.ic_youtube
            }

            imgIcon.setImageResource(platformIcon)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<SentimentData>) {
        items = newItems
        notifyDataSetChanged()
    }
}