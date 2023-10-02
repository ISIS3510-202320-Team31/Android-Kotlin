package com.example.hive.model.adapters

import android.app.Dialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.util.Resource
import com.example.hive.viewmodel.AddParticipatEventViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso

class CalendarHistoricalAdapter(private val viewModelAddParticipant: AddParticipatEventViewModel,
                                private val lifecycleOwner: LifecycleOwner,
                                private val sessionManager: SessionManager)  : RecyclerView.Adapter<CalendarHistoricalAdapter.MListHolder>(){

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

        cardView.setOnClickListener {
            val detailDialog = Dialog(holder.itemView.context)
            detailDialog.setContentView(R.layout.fragment_event_detail_historical)

            val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title_historical)
            eventNameTextView.text = event.name

            val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado_historical)
            if (event.state) {
                eventEstadoTextView.text = "Activo"
            } else {
                eventEstadoTextView.text = "Inactivo"
            }

            val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID_historical)
            eventIDTextView.text = event.id

            val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria_historical)
            eventCategoryTextView.text = event.category

            val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador_historical)
            eventCreatorTextView.text = event.creator

            val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha_historical)
            eventDateTextView.text = newDate

            val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion_historical)
            eventDuracionTextView.text = event.duration.toString()+" minutos"

            val eventDescriptionTextView = detailDialog.findViewById<TextView>(R.id.descripcion_historical)
            eventDescriptionTextView.text = event.description

            val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar_historical)
            eventLugarTextView.text = event.place

            val eventParticipantTextView = detailDialog.findViewById<TextView>(R.id.personas_historical)
            val stringParticipant = "${event.participants.size} / ${event.num_participants} personas"
            eventParticipantTextView.text = stringParticipant

            val userSession = sessionManager.getUserSession()

            val eventLinksInteresesTextView = detailDialog.findViewById<TextView>(R.id.linksInteres_historical)

            if (event.links.isNotEmpty()) {
                // If there are links, set the text to the links
                eventLinksInteresesTextView.text = event.links.joinToString("\n")
            } else {
                // If there are no links, set an empty text
                eventLinksInteresesTextView.text = ""
            }

            val eventImageView = detailDialog.findViewById<ImageView>(R.id.imagen_historical)
            val url = event.image
            if (url != null) {
                Picasso.get().load(url).into(eventImageView)
            }

            val eventQRImageView = detailDialog.findViewById<ImageView>(R.id.qr_historical)
            val eventId = event.id
            val qrCodeBitmap = generateQRCode(eventId, 300, 300)

            qrCodeBitmap?.let {
                eventQRImageView.setImageBitmap(it)
            }

            // Show the detail dialog
            detailDialog.show()
        }
    }

    private fun generateQRCode(eventId: String, width: Int, height: Int): Bitmap? {
        try {
            val hints = HashMap<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.Q

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, width, height, hints)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun submitList(list: List<EventResponse>) {
        differ.submitList(list)
    }
}