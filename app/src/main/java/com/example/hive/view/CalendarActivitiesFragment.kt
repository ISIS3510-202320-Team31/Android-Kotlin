package com.example.hive.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.util.Resource
import com.example.hive.model.adapters.CalendarAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.repository.EventRepository
import com.example.hive.viewmodel.CalendarListViewModel
import com.example.hive.viewmodel.CalendarViewModel
import com.example.hive.viewmodel.CalendarViewModelProviderFactory

class CalendarActivitiesFragment : Fragment() {

    companion object {
        fun newInstance() = CalendarActivitiesFragment()
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: CalendarViewModel
    private lateinit var viewModelCalendar: CalendarListViewModel
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_list, container, false)
        val repository = EventRepository()
        sessionManager = SessionManager(requireContext())
        val userSession = sessionManager.getUserSession()
        val viewModelFactory =
            userSession.userId?.let { CalendarViewModelProviderFactory(repository, it, "1") }
        viewModelCalendar = ViewModelProvider(this, viewModelFactory!!).get(CalendarListViewModel::class.java)

        //Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCalendarlist)
        calendarAdapter = CalendarAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = calendarAdapter

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressCalendar)

        //Observe LiveData from ViewModel
        viewModelCalendar.eventsPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading<*> -> {
                    //Show progress bar
                    loadingProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success<*> -> {
                    //Hide progress bar
                    loadingProgressBar.visibility = View.INVISIBLE
                    //Update list in adapter
                    resource.data?.let { calendarAdapter.submitList(it) }
                }
                is Resource.Error<*> -> {
                    //Hide progress bar
                    loadingProgressBar.visibility = View.INVISIBLE
                    //Show error message
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        return view

    }
}