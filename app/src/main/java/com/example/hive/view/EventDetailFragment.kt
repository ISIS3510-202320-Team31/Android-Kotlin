package com.example.hive.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.AddParticipatEventViewModel
import com.example.hive.viewmodel.AddParticipatEventViewModelProviderFactory
import com.example.hive.viewmodel.EventDetailViewModel

class EventDetailFragment : Fragment() {

    companion object {
        fun newInstance() = EventDetailFragment()
    }

    private lateinit var viewModel: EventDetailViewModel
    private lateinit var viewModelAddParticipant: AddParticipatEventViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val repository = EventRepository()
        val viewModelAddParticipatEventFactory = AddParticipatEventViewModelProviderFactory(repository)
        viewModelAddParticipant = ViewModelProvider(this, viewModelAddParticipatEventFactory).get(AddParticipatEventViewModel::class.java)
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventDetailViewModel::class.java)

        val joinEventButton = requireView().findViewById<Button>(R.id.submitButton)
        joinEventButton.setOnClickListener {
            val eventIDTextView = requireView().findViewById<TextView>(R.id.eventID)
            val eventID = eventIDTextView.text.toString()
            val userID = "0f2dfb8a-df34-4026-a989-6607d2b399b7"
            viewModelAddParticipant.addParticipatEventVM(eventID, userID)
            viewModelAddParticipant.addParticipatEvent.observe(viewLifecycleOwner, Observer { resource ->
                when (resource) {
                    is Resource.Loading<*> -> {
                    }
                    is Resource.Success<*> -> {
                        // change joinEventButton text to "salir"
                        joinEventButton.text = "No asistir"
                    }
                    is Resource.Error<*> -> {

                    }
                }
            })
        }
    }
}