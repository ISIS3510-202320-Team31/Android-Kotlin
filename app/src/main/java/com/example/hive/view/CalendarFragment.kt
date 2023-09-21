package com.example.hive.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.viewmodel.CalendarAdapter
import com.example.hive.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = CalendarFragment()
    }

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicialize RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        //Get a list of days starting from now
        val daysList = getDaysList()

        //Create and set the adapter
        val adapter = CalendarAdapter(daysList)
        recyclerView.adapter = adapter
    }

    private fun getDaysList(): List<String>{
        val daysList = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("EEE MMM dd, yyyy", Locale.getDefault())

        //Add days to the list, starting from now
        for (i in 0 until 30) {
            val formattedDate = dateFormat.format(calendar.time)
            daysList.add(formattedDate)
            calendar.add(Calendar.DAY_OF_MONTH,1)
        }
        return daysList
    }

}