package com.example.hive.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.network.responses.EventResponse

class EventsByDateAndUserAdapter: RecyclerView.Adapter<EventsByDateAndUserAdapter.MListHolder>() {

    inner class MListHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object: DiffUtil.ItemCallback<EventResponse>() {
        override fun areItemsTheSame(oldItem: EventResponse, newItem: EventResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventResponse, newItem: EventResponse): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MListHolder {
        return MListHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.event_card_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MListHolder, position: Int) {
        val event = differ.currentList[position]
        val cardView = holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.eventCardView)

        // Change date format to "dd/mm/yyyy"
        val date = event.date.split("-")
        val newDate = date[2] + "/" + date[1] + "/" + date[0]

        holder.itemView.apply {
            this.findViewById<android.widget.TextView>(R.id.creador).text = event.creator
            this.findViewById<android.widget.TextView>(R.id.nombre).text = event.name
            this.findViewById<android.widget.TextView>(R.id.descripcion).text = event.description
            this.findViewById<android.widget.TextView>(R.id.fecha).text = newDate
        }
    }

    fun submitList(list: List<EventResponse>) {
        differ.submitList(list)
    }
}