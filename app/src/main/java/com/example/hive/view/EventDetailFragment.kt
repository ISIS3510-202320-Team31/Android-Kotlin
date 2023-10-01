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
import com.example.hive.model.adapters.SessionManager
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
    private lateinit var sessionManager: SessionManager

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

        sessionManager = SessionManager(requireContext())
        val userID = sessionManager.getUserSession().userId

        val joinEventButton = requireView().findViewById<Button>(R.id.submitButton)

    }
}