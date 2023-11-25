package com.example.hive.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.adapters.CalendarHistoricalAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.room.entities.EventActivities
import com.example.hive.model.room.entities.EventHistorical
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class CalendarHistoricalFragment : Fragment() {
    companion object {
        fun newInstance() = CalendarActivitiesFragment()
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: CalendarViewModel
    private lateinit var viewModelCalendar: CalendarListViewModel
    private lateinit var calendarHistoricalAdapter: CalendarHistoricalAdapter
    private lateinit var viewModelAddParticipant: AddParticipatEventViewModel
    private lateinit var viewModelEventListOffline : EventListOfflineViewModel
    private lateinit var viewModelEventDetail: EventDetailViewModel
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var user: UserSession
    private var numberOfEvents: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_list, container, false)
        sessionManager = SessionManager(requireContext())
        user = sessionManager.getUserSession()

        val viewModelAddPaticipantEventFactory = context?.let {
            AddParticipatEventViewModelProviderFactory(
                it
            )
        }
        viewModelAddParticipant =
            viewModelAddPaticipantEventFactory?.let {
                ViewModelProvider(this,
                    it
                ).get(AddParticipatEventViewModel::class.java)
            }!!

        //Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCalendarlist)
        calendarHistoricalAdapter = context?.let{
            CalendarHistoricalAdapter(this, it)
        }!!
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = calendarHistoricalAdapter

        //Set up ViewModel
        val viewModelFactoryOffline = context?.let{
            EventListOfflineViewModelProviderFactory(
                it
            )
        }

        viewModelEventListOffline =
            viewModelFactoryOffline?.let {
                ViewModelProvider(this,
                    it
                ).get(EventListOfflineViewModel::class.java)
            }!!

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressCalendar)

        //Observe LiveData from ViewModel
        viewModelEventListOffline.allEventHistorical?.observe(viewLifecycleOwner, Observer { resource ->
            loadingProgressBar.visibility = View.GONE
            val list = mutableListOf<EventResponse>()
            resource.let {
                println("Resource: $it")
                // Filter the list for only the ones with date of today and after
                val today = Calendar.getInstance()
                val filteredList = it.filter { event ->
                    val eventDate = Calendar.getInstance()

                    //Transform the date from the event from string to Date
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    val date = formatter.parse(event.date)
                    eventDate.time = date
                    eventDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
                }

                // Cast manually the list of events to the list of EventResponse
                for (event in filteredList) {
                    val eventToAdd = EventResponse(
                        event.id,
                        event.image ?: "",
                        event.name ?: "",
                        event.description ?: "",
                        event.date ?: "",
                        event.place ?: "",
                        event.num_participants ?: 0,
                        event.category ?: "",
                        event.state ?: false,
                        event.duration ?: 0,
                        event.creator_id ?: "",
                        event.creator ?: "",
                        event.participants ?: emptyList(),
                        event.links ?: emptyList()
                    )
                    list.add(eventToAdd)
                }

                calendarHistoricalAdapter.submitList(list)

                // Update the number of events
                numberOfEvents = list.size

                // Show the number of events
                val numberOfEventsTextView =
                    view.findViewById<TextView>(R.id.numberOfEventsTextView)
                numberOfEventsTextView.text = getString(R.string.number_of_events) +" "+ numberOfEvents.toString()
            }
        })
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        val loadingProgressBar = view?.findViewById<ProgressBar>(R.id.loadingProgressCalendar)
        connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                val viewModelFactory =
                    user.userId?.let {
                        context?.let { it1 ->
                            CalendarViewModelProviderFactory(
                                it, "0",
                                it1
                            )
                        }
                    }

                viewModelCalendar = viewModelFactory?.let {
                    ViewModelProvider(
                        this,
                        it
                    ).get(CalendarListViewModel::class.java)
                }!!

                viewModelCalendar.eventsPage.observe(viewLifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Loading<*> -> {
                            // Show progress bar
                            if (loadingProgressBar != null) {
                                loadingProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Success<*> -> {
                            if (loadingProgressBar != null) {
                                loadingProgressBar.visibility = View.GONE
                            }
                            // Update the RecyclerView with the list of events
                            resource.data?.let {
                                // Filter the list for only the ones with date of today and after
                                val today = Calendar.getInstance()
                                val filteredList = it.filter { event ->
                                    val eventDate = Calendar.getInstance()
                                    //Transform the date from the event from string to Date
                                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                                    val date = formatter.parse(event.date)
                                    eventDate.time = date
                                    eventDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
                                }
                                calendarHistoricalAdapter.submitList(filteredList)
                            }

                            viewModelEventListOffline.removeEventHistoricalDatabase()

                            // Loop over the filtered list and create an EventEntity for each event
                            resource.data?.forEach { event ->
                                val eventToAdd = EventHistorical(
                                    event.id,
                                    event.image ?: "",
                                    event.name ?: "",
                                    event.description ?: "",
                                    event.date ?: "",
                                    event.place ?: "",
                                    event.num_participants ?: 0,
                                    event.category ?: "",
                                    event.state ?: false,
                                    event.duration ?: 0,
                                    event.creator_id ?: "",
                                    event.creator ?: "",
                                    event.participants ?: emptyList(),
                                    event.links ?: emptyList()
                                )
                                viewModelEventListOffline.insertOneToDatabaseHistorical(eventToAdd)
                            }
                        }
                        is Resource.Error<*> -> {
                            // Handle error state (e.g., show an error message)
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })

            } else {
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        })

        val cardView = requireView().findViewById<View>(R.id.recyclerCalendarlist)
        cardView.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.fragment_event_detail_historical)
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.evento_cancelado),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val viewModelFactoryDetail =
                    context?.let { EventDetailViewModelProviderFactory(result.contents, it) }
                viewModelEventDetail = viewModelFactoryDetail?.let {
                    ViewModelProvider(
                        this,
                        it
                    ).get(EventDetailViewModel::class.java)
                }!!

                viewModelEventDetail.eventById.observe(viewLifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Loading<*> -> {
                            // Show progress bar
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success<*> -> {
                            // Show dialog
                            // asosiate the id of the event to the dialog

                            val detailDialog = Dialog(requireContext())

                            detailDialog.setContentView(R.layout.fragment_event_detail_historical)

                            val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title_historical)
                            eventNameTextView.text = resource.data?.name

                            val eventEstadoTextView =
                                detailDialog.findViewById<TextView>(R.id.estado_historical)
                            if (resource.data?.state == true) {
                                eventEstadoTextView.text =
                                    requireContext().getString(R.string.event_state_activo)
                            } else {
                                eventEstadoTextView.text =
                                    requireContext().getString(R.string.event_state_inactivo)
                            }

                            val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID_historical)
                            eventIDTextView.text = resource.data?.id

                            val eventCategoryTextView =
                                detailDialog.findViewById<TextView>(R.id.categoria_historical)
                            eventCategoryTextView.text = resource.data?.category

                            val eventCreatorTextView =
                                detailDialog.findViewById<TextView>(R.id.creador_historical)
                            eventCreatorTextView.text = resource.data?.creator

                            val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha_historical)
                            eventDateTextView.text = resource.data?.date

                            val eventDuracionTextView =
                                detailDialog.findViewById<TextView>(R.id.duracion_historical)
                            eventDuracionTextView.text =
                                resource.data?.duration.toString() + " " + requireContext().getString(
                                    R.string.event_duration_minutos
                                )

                            val eventDescriptionTextView =
                                detailDialog.findViewById<TextView>(R.id.descripcion_historical)
                            eventDescriptionTextView.text = resource.data?.description

                            val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar_historical)
                            eventLugarTextView.text = resource.data?.place

                            val eventParticipantTextView =
                                detailDialog.findViewById<TextView>(R.id.personas_historical)
                            val stringParticipant =
                                "${resource.data?.participants?.size} / ${resource.data?.num_participants} " + requireContext().getString(
                                    R.string.event_participants_personas
                                )
                            eventParticipantTextView.text = stringParticipant

                            val eventLinksInteresesTextView =
                                detailDialog.findViewById<TextView>(R.id.linksInteres_historical)

                            val eventParticipants = resource.data?.participants

                            if (resource.data?.links?.isNotEmpty() == true) {
                                // If there are links, set the text to the links
                                eventLinksInteresesTextView.text =
                                    resource.data.links.joinToString("\n")
                            } else {
                                // If there are no links, set an empty text
                                eventLinksInteresesTextView.text = ""
                            }

                            val eventImageView = detailDialog.findViewById<ImageView>(R.id.imagen_historical)
                            val url = resource.data?.image
                            if (url != null) {
                                try {
                                    Picasso.get().load(url).into(eventImageView)
                                } catch (e: Exception) {
                                    eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day)
                                }
                            }
                            val qrImageView = detailDialog.findViewById<ImageView>(R.id.qr_historical)
                            val eventId = resource.data?.id.toString()
                            val qrCodeBitmap = generateQRCode(eventId, 300, 300)
                            qrImageView.setImageBitmap(qrCodeBitmap)

                            detailDialog.show()
                        }
                        is Resource.Error<*> -> {
                            // Handle error state (e.g., show an error message)
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                    )
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}