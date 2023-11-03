package com.example.hive.model.adapters

import android.app.Dialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.AddParticipatEventViewModel
import com.example.hive.viewmodel.EventDetailViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso

class EventsAdapter(private val viewModelAddParticipant: AddParticipatEventViewModel, private val lifecycleOwner: LifecycleOwner, private val sessionManager: SessionManager) : RecyclerView.Adapter<EventsAdapter.MListHolder>() {

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

            val eventRepository = EventRepository()

            eventDetailViewModel = EventDetailViewModel(eventRepository, event.id)

            eventDetailViewModel.getEventByIdVM(event.id)

            val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title)
            val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado)
            val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID)
            val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria)
            val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador)
            val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha)
            val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion)
            val eventDescriptionTextView = detailDialog.findViewById<TextView>(R.id.descripcion)
            val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar)
            val eventParticipantTextView = detailDialog.findViewById<TextView>(R.id.personas)
            val eventQRImageView = detailDialog.findViewById<ImageView>(R.id.qr)
            val joinEventButton = detailDialog.findViewById<Button>(R.id.submitButton)
            val eventLinksInteresesTextView = detailDialog.findViewById<TextView>(R.id.linksInteres)
            val eventImageView = detailDialog.findViewById<ImageView>(R.id.imagen)

            eventDetailViewModel.eventById.observe(lifecycleOwner, Observer { resource ->
                when (resource) {
                    is Resource.Loading<*> -> {
                        // Show progress bar
                        // Hide all components except progress bar
                        val dialogLinearLayout = detailDialog.findViewById<LinearLayout>(R.id.layoutCard)
                        val dialogProgressBar = detailDialog.findViewById<ProgressBar>(R.id.loadingProgressBar)

                        dialogLinearLayout.visibility = View.GONE
                        dialogProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success<*> -> {

                        // Hide progress bar
                        // Show all components except progress bar
                        val dialogLinearLayout = detailDialog.findViewById<LinearLayout>(R.id.layoutCard)
                        val dialogProgressBar = detailDialog.findViewById<ProgressBar>(R.id.loadingProgressBar)

                        dialogLinearLayout.visibility = View.VISIBLE
                        dialogProgressBar.visibility = View.GONE

                        val eventDetailResponse = resource.data as EventDetailResponse

                        eventNameTextView.text = eventDetailResponse.name

                        if (eventDetailResponse.state) {
                            eventEstadoTextView.text = "Activo"
                        } else {
                            eventEstadoTextView.text = "Inactivo"
                        }

                        eventIDTextView.text = eventDetailResponse.id

                        eventCategoryTextView.text = eventDetailResponse.category

                        eventCreatorTextView.text = eventDetailResponse.creator

                        eventDateTextView.text = newDate

                        eventDuracionTextView.text = eventDetailResponse.duration.toString()+" minutos"

                        eventDescriptionTextView.text = eventDetailResponse.description

                        eventLugarTextView.text = eventDetailResponse.place

                        val stringParticipant = "${eventDetailResponse.participants.size} / ${eventDetailResponse.num_participants} personas"
                        eventParticipantTextView.text = stringParticipant

                        val eventId = eventDetailResponse.id
                        val qrCodeBitmap = generateQRCode(eventId, 300, 300)

                        qrCodeBitmap?.let {
                            eventQRImageView.setImageBitmap(it)
                        }

                        val userSession = sessionManager.getUserSession()

                        // Check if userSession.id is in event.participants
                        if (eventDetailResponse.participants.contains(userSession.userId)) {
                            // If userSession.id is in event.participants, change joinEventButton text to "salir"
                            joinEventButton.text = "No asistir"
                        }

                        if (eventDetailResponse.links.isNotEmpty()) {
                            // If there are links, set the text to the links
                            eventLinksInteresesTextView.text = eventDetailResponse.links.joinToString("\n")
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

                        joinEventButton.setOnClickListener {
                            val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID)
                            val eventID = eventIDTextView.text.toString()
                            val userID = userSession.userId
                            if (userID != null && joinEventButton.text.toString() == "Unirse") {
                                viewModelAddParticipant.addParticipatEventVM(eventID, userID)
                                viewModelAddParticipant.addParticipatEvent.observe(lifecycleOwner, Observer { resource ->
                                    when (resource) {
                                        is Resource.Loading<*> -> {
                                        }
                                        is Resource.Success<*> -> {

                                            if (!eventDetailResponse.participants.contains(userID)) {
                                                eventDetailResponse.participants+=userID
                                            }

                                            // change joinEventButton text to "salir"
                                            joinEventButton.text = "No asistir"

                                            // update the number of participants
                                            val stringParticipant = "${eventDetailResponse.participants.size} / ${eventDetailResponse.num_participants} personas"
                                            eventParticipantTextView.text = stringParticipant
                                        }
                                        is Resource.Error<*> -> {

                                        }
                                    }
                                })
                            }
                            if (userID != null && joinEventButton.text.toString() == "No asistir") {
                                viewModelAddParticipant.deleteParticipatEventVM(eventID, userID)
                                // Remove the ID from the event.participants
                                viewModelAddParticipant.deleteParticipatEvent.observe(lifecycleOwner, Observer { resource ->
                                    when (resource) {
                                        is Resource.Loading<*> -> {
                                        }
                                        is Resource.Success<*> -> {
                                            if (eventDetailResponse.participants.contains(userID)) {
                                                eventDetailResponse.participants-=userID
                                            }
                                            // change joinEventButton text to "unirse"
                                            joinEventButton.text = "Unirse"

                                            // update the number of participants
                                            val stringParticipant = "${eventDetailResponse.participants.size} / ${eventDetailResponse.num_participants} personas"
                                            eventParticipantTextView.text = stringParticipant

                                        }
                                        is Resource.Error<*> -> {

                                        }
                                    }
                                })
                            }
                        }

                    }
                    is Resource.Error<*> -> {
                        // Handle error state (e.g., show an error message)
                    }
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