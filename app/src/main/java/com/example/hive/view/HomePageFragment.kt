package com.example.hive.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hive.R
import com.example.hive.model.adapters.EventsAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomePageFragment : Fragment() {

    companion object {
        fun newInstance() = HomePageFragment()
    }

    private lateinit var viewModel: HomePageViewModel
    private lateinit var viewModelEvent: EventListViewModel
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var viewModelEventDetail: EventDetailViewModel
    private lateinit var viewModelAddParticipant: AddParticipatEventViewModel
    private lateinit var user : UserSession


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        // Refresh fragment
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            refreshFragment(swipeRefreshLayout)
        }

        val userSession = SessionManager(requireContext())
        user = userSession.getUserSession()
        val viewModelFactory = EventsViewModelProviderFactory(user)
        viewModelEvent = ViewModelProvider(this, viewModelFactory).get(EventListViewModel::class.java)

        val viewModelAddParticipatEventFactory = AddParticipatEventViewModelProviderFactory()
        viewModelAddParticipant = ViewModelProvider(this, viewModelAddParticipatEventFactory).get(AddParticipatEventViewModel::class.java)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        eventsAdapter = EventsAdapter(viewModelAddParticipant, this, userSession)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = eventsAdapter

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)

        // Observe LiveData from ViewModel
        viewModelEvent.eventsPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading<*> -> {
                    // Show progress bar
                    loadingProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success<*> -> {
                    loadingProgressBar.visibility = View.GONE
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
                            eventDate.get(Calendar.DAY_OF_YEAR) >= today.get(Calendar.DAY_OF_YEAR)
                        }

                        eventsAdapter.submitList(filteredList) }
                }
                is Resource.Error<*> -> {
                    // Handle error state (e.g., show an error message)
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        val cardView = requireView().findViewById<View>(R.id.recyclerView)
        cardView.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.fragment_event_detail)
            dialog.show()
        }

        val scanQRButton = requireView().findViewById<View>(R.id.scanQRButton)
        scanQRButton.setOnClickListener { initScanner()}
    }

    private fun initScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this) // Cambiar a forSupportFragment si usas AndroidX
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setBeepEnabled(false)
        integrator.setPrompt(requireContext().getString(R.string.event_promotion))
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.evento_cancelado), Toast.LENGTH_LONG).show()
                } else {
                    val viewModelFactoryDetail = EventDetailViewModelProviderFactory(result.contents)
                    viewModelEventDetail = ViewModelProvider(this, viewModelFactoryDetail).get(EventDetailViewModel::class.java)

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

                                detailDialog.setContentView(R.layout.fragment_event_detail)

                                val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title)
                                eventNameTextView.text = resource.data?.name

                                val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado)
                                if (resource.data?.state == true) {
                                    eventEstadoTextView.text = requireContext().getString(R.string.event_state_activo)
                                } else {
                                    eventEstadoTextView.text = requireContext().getString(R.string.event_state_inactivo)
                                }

                                val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID)
                                eventIDTextView.text = resource.data?.id

                                val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria)
                                eventCategoryTextView.text = resource.data?.category

                                val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador)
                                eventCreatorTextView.text = resource.data?.creator

                                val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha)
                                eventDateTextView.text = resource.data?.date

                                val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion)
                                eventDuracionTextView.text = resource.data?.duration.toString()+" "+ requireContext().getString(R.string.event_duration_minutos)

                                val eventDescriptionTextView = detailDialog.findViewById<TextView>(R.id.descripcion)
                                eventDescriptionTextView.text = resource.data?.description

                                val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar)
                                eventLugarTextView.text = resource.data?.place

                                val eventParticipantTextView = detailDialog.findViewById<TextView>(R.id.personas)
                                val stringParticipant = "${resource.data?.participants?.size} / ${resource.data?.num_participants} "+ requireContext().getString(R.string.event_participants_personas)
                                eventParticipantTextView.text = stringParticipant

                                val eventLinksInteresesTextView = detailDialog.findViewById<TextView>(R.id.linksInteres)

                                val eventParticipants = resource.data?.participants

                                if (resource.data?.links?.isNotEmpty() == true) {
                                    // If there are links, set the text to the links
                                    eventLinksInteresesTextView.text = resource.data.links.joinToString("\n")
                                } else {
                                    // If there are no links, set an empty text
                                    eventLinksInteresesTextView.text = ""
                                }

                                val eventImageView = detailDialog.findViewById<ImageView>(R.id.imagen)
                                val url = resource.data?.image
                                if (url != null) {
                                    try {
                                        Picasso.get().load(url).into(eventImageView)
                                    } catch (e: Exception) {
                                        eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day)
                                    }
                                }


                                val qrImageView = detailDialog.findViewById<ImageView>(R.id.qr)
                                val eventId = resource.data?.id.toString()
                                val qrCodeBitmap = generateQRCode(eventId, 300, 300)
                                qrImageView.setImageBitmap(qrCodeBitmap)

                                val joinEventButton = detailDialog.findViewById<Button>(R.id.submitButton)

                                if (eventParticipants?.contains(user.userId) == true) {
                                    joinEventButton.text = requireContext().getString(R.string.event_detail_no_asistir)
                                } else {
                                    joinEventButton.text = requireContext().getString(R.string.event_detail_unirse)
                                }
                                    joinEventButton.setOnClickListener {
                                    val eventIDTextView = detailDialog.findViewById<TextView>(R.id.eventID)
                                    val eventID = eventIDTextView.text.toString()
                                    val userID = user.userId
                                    if (userID != null && joinEventButton.text.toString() == requireContext().getString(R.string.event_detail_unirse)) {
                                        viewModelAddParticipant.addParticipatEventVM(eventID, userID)
                                        viewModelAddParticipant.addParticipatEvent.observe(this, Observer { resource ->
                                            when (resource) {
                                                is Resource.Loading<*> -> {
                                                }
                                                is Resource.Success<*> -> {

                                                    if (!resource.data?.participants?.contains(userID)!!) {
                                                        resource.data?.participants = resource.data?.participants?.plus(
                                                            userID
                                                        )!!
                                                    }

                                                    // change joinEventButton text to "salir"
                                                    joinEventButton.text = requireContext().getString(R.string.event_detail_no_asistir)

                                                    // update the number of participants
                                                    val stringParticipant = "${resource.data?.participants!!.size} / ${resource.data?.num_participants} "+requireContext().getString(R.string.event_participants_personas)
                                                    eventParticipantTextView.text = stringParticipant
                                                }
                                                is Resource.Error<*> -> {

                                                }
                                            }
                                        })
                                    }
                                    if (userID != null && joinEventButton.text.toString() == requireContext().getString(R.string.event_detail_no_asistir)) {
                                        viewModelAddParticipant.deleteParticipatEventVM(eventID, userID)
                                        // Remove the ID from the event.participants
                                        viewModelAddParticipant.deleteParticipatEvent.observe(this, Observer { resource ->
                                            when (resource) {
                                                is Resource.Loading<*> -> {
                                                }
                                                is Resource.Success<*> -> {
                                                    if (resource.data?.participants?.contains(userID) == true) {
                                                        resource.data.participants = resource.data.participants?.minus(
                                                            userID
                                                        )!!
                                                    }
                                                    // change joinEventButton text to "unirse"
                                                    joinEventButton.text = requireContext().getString(R.string.event_detail_unirse)

                                                    // update the number of participants
                                                    val stringParticipant = "${resource.data?.participants?.size} / ${resource.data?.num_participants} "+requireContext().getString(R.string.event_participants_personas)
                                                    eventParticipantTextView.text = stringParticipant

                                                }
                                                is Resource.Error<*> -> {

                                                }
                                            }
                                        })
                                    }


                                }



                                detailDialog.show()
                            }
                            is Resource.Error<*> -> {
                                // Handle error state (e.g., show an error message)
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
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
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun refreshFragment( swipeRefreshLayout: SwipeRefreshLayout ) {
        swipeRefreshLayout.isRefreshing = true

        viewModelEvent.getEventsVM()

        viewModelEvent.eventsPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading<*> -> {
                    // loading indicator will be kept
                }
                is Resource.Success<*> -> {
                    // Stop the loading indicator once the data has been loaded
                    swipeRefreshLayout.isRefreshing = false
                    // Manage the success state (e.g., show an success message)
                    Toast.makeText(requireContext(), getString(R.string.swipe_down_success), Toast.LENGTH_SHORT).show()
                }
                is Resource.Error<*> -> {
                    // Stop the loading indicator in case of error
                    swipeRefreshLayout.isRefreshing = false
                    // Manage the error state (e.g., show an error message)
                    Toast.makeText(requireContext(), getString(R.string.swipe_down_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}