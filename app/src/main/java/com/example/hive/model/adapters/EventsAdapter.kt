package com.example.hive.model.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hive.R
import com.example.hive.model.network.responses.EventResponse

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.MListHolder>() {

    inner class MListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<EventResponse>() {
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

        // Add a click listener to the card view
        cardView.setOnClickListener {
            val detailDialog = Dialog(holder.itemView.context)
            detailDialog.setContentView(R.layout.fragment_event_detail)

            val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title)
            eventNameTextView.text = event.name

            val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado)
            if (event.state) {
                eventEstadoTextView.text = "Activo"
            } else {
                eventEstadoTextView.text = "Inactivo"
            }

            val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria)
            eventCategoryTextView.text = event.category

            val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador)
            eventCreatorTextView.text = event.creator

            val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha)
            eventDateTextView.text = newDate

            val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion)
            eventDuracionTextView.text = event.duration.toString()+" minutos"

            val eventDescriptionTextView = detailDialog.findViewById<TextView>(R.id.descripcion)
            eventDescriptionTextView.text = event.description

            val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar)
            eventLugarTextView.text = event.place

            val eventParticipantTextView = detailDialog.findViewById<TextView>(R.id.personas)
            val stringParticipant = "${event.participants.size} / ${event.num_participants} personas"
            eventParticipantTextView.text = stringParticipant

            // Show the detail dialog
            detailDialog.show()
        }

    }

    fun submitList(list: List<EventResponse>) {
        differ.submitList(list)
    }
}