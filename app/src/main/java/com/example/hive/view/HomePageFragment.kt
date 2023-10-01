package com.example.hive.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.adapters.EventsAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*
import com.google.zxing.integration.android.IntentIntegrator
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        // Initialize ViewModel
        val repository = EventRepository()
        val userSession = SessionManager(requireContext())
        val user = userSession.getUserSession()
        val viewModelFactory = EventsViewModelProviderFactory(repository, user)
        viewModelEvent = ViewModelProvider(this, viewModelFactory).get(EventListViewModel::class.java)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        eventsAdapter = EventsAdapter()
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
        integrator.setPrompt("Unete a un Evento")
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val repository = EventRepository()
                    val viewModelFactoryDetail = EventDetailViewModelProviderFactory(repository, result.contents)
                    viewModelEventDetail = ViewModelProvider(this, viewModelFactoryDetail).get(EventDetailViewModel::class.java)

                    viewModelEventDetail.eventById.observe(viewLifecycleOwner, Observer { resource ->
                        when (resource) {
                            is Resource.Loading<*> -> {
                                // Show progress bar
                                // loadingProgressBar.visibility = View.VISIBLE
                            }
                            is Resource.Success<*> -> {
                                // Show dialog
                                val detailDialog = Dialog(requireContext())
                                detailDialog.setContentView(R.layout.fragment_event_detail)

                                val eventNameTextView = detailDialog.findViewById<TextView>(R.id.title)
                                eventNameTextView.text = resource.data?.name

                                val eventEstadoTextView = detailDialog.findViewById<TextView>(R.id.estado)
                                if (resource.data?.state == true) {
                                    eventEstadoTextView.text = "Activo"
                                } else {
                                    eventEstadoTextView.text = "Inactivo"
                                }

                                val eventCategoryTextView = detailDialog.findViewById<TextView>(R.id.categoria)
                                eventCategoryTextView.text = resource.data?.category

                                val eventCreatorTextView = detailDialog.findViewById<TextView>(R.id.creador)
                                eventCreatorTextView.text = resource.data?.creator?.name

                                val eventDateTextView = detailDialog.findViewById<TextView>(R.id.fecha)
                                eventDateTextView.text = resource.data?.date

                                val eventDuracionTextView = detailDialog.findViewById<TextView>(R.id.duracion)
                                eventDuracionTextView.text = resource.data?.duration.toString()+" minutos"

                                val eventDescriptionTextView = detailDialog.findViewById<TextView>(R.id.descripcion)
                                eventDescriptionTextView.text = resource.data?.description

                                val eventLugarTextView = detailDialog.findViewById<TextView>(R.id.lugar)
                                eventLugarTextView.text = resource.data?.place

                                val eventParticipantTextView = detailDialog.findViewById<TextView>(R.id.personas)
                                val stringParticipant = "${resource.data?.participants?.size} / ${resource.data?.num_participants} personas"
                                eventParticipantTextView.text = stringParticipant

                                detailDialog.show()
                            }
                            is Resource.Error<*> -> {
                                // Handle error state (e.g., show an error message)
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })

                    Toast.makeText(requireContext(), "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}