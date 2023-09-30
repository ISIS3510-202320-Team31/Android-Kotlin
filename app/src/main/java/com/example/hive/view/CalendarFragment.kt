package com.example.hive.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.adapters.EventsByDateAdapter
import com.example.hive.model.adapters.EventsByDateAndUserAdapter
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*

class CalendarFragment : Fragment() {

    companion object {
        fun newInstance() = CalendarFragment()
    }

    private lateinit var viewModel: CalendarViewModel
    private lateinit var viewModelCalendar: CalendarListViewModel
    private lateinit var eventsByDateAndUserAdapter: EventsByDateAndUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize ViewModel
        val repository = EventRepository()
        val viewModelFactory = CalendarViewModelProviderFactory(repository,"d961dbbb-84bb-454a-aa78-abbba3a8d6a7", "1")
        viewModelCalendar =
            ViewModelProvider(this, viewModelFactory).get(CalendarListViewModel::class.java)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        eventsByDateAndUserAdapter = EventsByDateAndUserAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = eventsByDateAndUserAdapter

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)

        // Observe LiveData from ViewModel (eventsByDate)
        viewModelCalendar.eventsByDate.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading<*> -> {
                    // Show progress bar
                    loadingProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success<*> -> {
                    loadingProgressBar.visibility = View.GONE
                    // Update the RecyclerView with the list of events grouped by date
                    resource.data?.let { groupedEvents ->
                        //Flatten the list of pairs into a single list of events
                        val events = groupedEvents.flatMap { it.second }
                        eventsByDateAndUserAdapter.submitList(events)
                    }
                }
                is Resource.Error<*> -> {
                    // Handle error state (e.g., show an error message)
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        return view
    }
}