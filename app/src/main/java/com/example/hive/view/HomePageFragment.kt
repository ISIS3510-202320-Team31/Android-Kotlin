package com.example.hive.view

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.adapters.EventsAdapter
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.EventListViewModel
import com.example.hive.viewmodel.EventsViewModelProviderFactory
import com.example.hive.viewmodel.HomePageViewModel

class HomePageFragment : Fragment() {

    companion object {
        fun newInstance() = HomePageFragment()
    }

    private lateinit var viewModel: HomePageViewModel
    private lateinit var viewModelEvent: EventListViewModel
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        // Initialize ViewModel
        val repository = EventRepository()
        val viewModelFactory = EventsViewModelProviderFactory(repository)
        viewModelEvent = ViewModelProvider(this, viewModelFactory).get(EventListViewModel::class.java)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        eventsAdapter = EventsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = eventsAdapter

        // Observe LiveData from ViewModel
        viewModelEvent.eventsPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading<*> -> {
                    // Handle loading state (e.g., show a progress bar)
                }
                is Resource.Success<*> -> {
                    // Update the RecyclerView with the list of events
                    resource.data?.let { eventsAdapter.submitList(it) }
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
    }

}