package com.example.hive.model.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.AddParticipatEventViewModel
import com.example.hive.viewmodel.EventDetailViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso

class CalendarHistoricalAdapter(private val lifecycleOwner: LifecycleOwner,
                                private val context: Context): RecyclerView.Adapter<CalendarHistoricalAdapter.MListHolder>(){

    inner class MListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<EventResponse>() {
        override fun areItemsTheSame(oldItem: EventResponse, newItem: EventResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventResponse, newItem: EventResponse): Boolean {
            return oldItem == newItem
        }
    }

    private lateinit var eventDetailViewModel: EventDetailViewModel
    private lateinit var connectionLiveData: ConnectionLiveData

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

        connectionLiveData = ConnectionLiveData(context)
        val detailDialog = Dialog(holder.itemView.context)
        detailDialog.setContentView(R.layout.fragment_no_internet_connection)

        cardView.setOnClickListener {
            connectionLiveData.observe(lifecycleOwner, Observer { isConnected ->
                if (isConnected) {
                    // If the user is connected to the internet, show the event detail

                    eventDetailViewModel = EventDetailViewModel(event.id, context)

                    eventDetailViewModel.getEventByIdVM(event.id)

                    detailDialog.setContentView(R.layout.fragment_event_detail_historical)

                    val dialogLinearLayout =
                        detailDialog.findViewById<LinearLayout>(R.id.layoutCardHistorical)
                    val dialogProgressBar =
                        detailDialog.findViewById<ProgressBar>(R.id.loadingProgressCalendar)

                    dialogLinearLayout.visibility = View.GONE
                    dialogProgressBar.visibility = View.VISIBLE

                    val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title_historical)
                    val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado_historical)
                    if (event.state) {
                        eventEstadoTextView.text =
                            holder.itemView.context.getString(R.string.event_state_activo)
                    } else {
                        eventEstadoTextView.text =
                            holder.itemView.context.getString(R.string.event_state_inactivo)
                    }

                    val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID_historical)
                    val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria_historical)
                    val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador_historical)
                    val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha_historical)
                    val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion_historical)
                    eventDuracionTextView.text =
                        event.duration.toString() + " " + holder.itemView.context.getString(R.string.event_duration_minutos)

                    val eventDescriptionTextView =
                        detailDialog.findViewById<TextView>(R.id.descripcion_historical)
                    val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar_historical)
                    val eventParticipantTextView =
                        detailDialog.findViewById<TextView>(R.id.personas_historical)
                    val eventQRImageView = detailDialog.findViewById<ImageView>(R.id.qr_historical)
                    val eventLinksInteresesTextView =
                        detailDialog.findViewById<TextView>(R.id.linksInteres_historical)
                    val eventImageView = detailDialog.findViewById<ImageView>(R.id.imagen_historical)

                    eventDetailViewModel.eventById.observe(lifecycleOwner, Observer { resource ->
                        when (resource) {
                            is Resource.Loading<*> -> {
                                // Show progress bar
                                // Hide all components except progress bar
                                val dialogLinearLayout =
                                    detailDialog.findViewById<LinearLayout>(R.id.layoutCardHistorical)
                                val dialogProgressBar =
                                    detailDialog.findViewById<ProgressBar>(R.id.loadingProgressCalendar)

                                dialogLinearLayout.visibility = View.GONE
                                dialogProgressBar.visibility = View.VISIBLE
                            }

                            is Resource.Success<*> -> {

                                // Hide progress bar
                                // Show all components except progress bar
                                val dialogLinearLayout =
                                    detailDialog.findViewById<LinearLayout>(R.id.layoutCardHistorical)
                                val dialogProgressBar =
                                    detailDialog.findViewById<ProgressBar>(R.id.loadingProgressCalendar)

                                dialogProgressBar.visibility = View.GONE
                                dialogLinearLayout.visibility = View.VISIBLE

                                val eventDetailResponse = resource.data as EventDetailResponse

                                eventNameTextView.text = eventDetailResponse.name

                                if (eventDetailResponse.state) {
                                    eventEstadoTextView.text =
                                        holder.itemView.context.getString(R.string.event_state_activo)
                                } else {
                                    eventEstadoTextView.text =
                                        holder.itemView.context.getString(R.string.event_state_inactivo)
                                }

                                eventIDTextView.text = eventDetailResponse.id

                                eventCategoryTextView.text = eventDetailResponse.category

                                eventCreatorTextView.text = eventDetailResponse.creator

                                eventDateTextView.text = newDate

                                eventDuracionTextView.text =
                                    eventDetailResponse.duration.toString() + " " + holder.itemView.context.getString(
                                        R.string.event_duration_minutos
                                    )

                                eventDescriptionTextView.text = eventDetailResponse.description

                                eventLugarTextView.text = eventDetailResponse.place

                                val stringParticipant =
                                    "${eventDetailResponse.participants.size} / ${eventDetailResponse.num_participants} " + holder.itemView.context.getString(
                                        R.string.event_participants_personas
                                    )
                                eventParticipantTextView.text = stringParticipant

                                val eventId = eventDetailResponse.id
                                val qrCodeBitmap = generateQRCode(eventId, 300, 300)

                                qrCodeBitmap?.let {
                                    eventQRImageView.setImageBitmap(it)
                                }

                                if (eventDetailResponse.links.isNotEmpty()) {
                                    // If there are links, set the text to the links
                                    eventLinksInteresesTextView.text =
                                        eventDetailResponse.links.joinToString("\n")
                                } else {
                                    // If there are no links, set an empty text
                                    eventLinksInteresesTextView.text = ""
                                }

                                val url = eventDetailResponse.image

                                if (url != null || url != "") {

                                    //In case it is not a loadable image just leave the eventImageView empty using a try catch
                                    try {
                                        Picasso.get().load(url).into(eventImageView)
                                    } catch (e: Exception) {
                                        eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day)
                                    }
                                }

                            }
                            is Resource.Error<*> -> {
                                // Handle error state (e.g., show an error message)
                                detailDialog.setContentView(R.layout.fragment_no_internet_connection)
                            }
                        }
                    })
                } else {
                    detailDialog.setContentView(R.layout.fragment_no_internet_connection)
                }
            })
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