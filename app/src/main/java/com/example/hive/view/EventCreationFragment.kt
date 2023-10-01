package com.example.hive.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.repository.EventRepository
import com.example.hive.viewmodel.EventCreationViewModel
import com.example.hive.viewmodel.EventCreationViewModelProviderFactory

class EventCreationFragment : Fragment() {

    private lateinit var eventRepository: EventRepository
    private lateinit var eventCreationViewModelProviderFactory: EventCreationViewModelProviderFactory
    private lateinit var eventCreationViewModel: EventCreationViewModel


    companion object {
        fun newInstance() = EventCreationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Ojala me muera pa no tener que hacer mas moviles
        //eventRepository = EventRepository()
        //val eventCreationViewModelProviderFactory = EventCreationViewModelProviderFactory(eventRepository)
        //eventCreationViewModel = ViewModelProvider(this, eventCreationViewModelProviderFactory).get(eventCreationViewModel::class.java)
        return inflater.inflate(R.layout.fragment_event_creation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val buttonCreateEvent = view?.findViewById<Button>(R.id.submitButton)
        val session = SessionManager(requireContext())

        buttonCreateEvent?.setOnClickListener{
            //val image
            val name = view?.findViewById<EditText>(R.id.inputBox)
            val place = view?.findViewById<EditText>(R.id.inputBoxLugar)
            val date = view?.findViewById<CalendarView>(R.id.calendarView)
            val description = view?.findViewById<EditText>(R.id.textBoxDescription)
            val num_participants = view?.findViewById<EditText>(R.id.inputBoxParticipant)
            val category = view?.findViewById<Spinner>(R.id.spinner1)
            val state = true
            val duration = view?.findViewById<EditText>(R.id.inputBoxDuration)
            val creador = session.getUserSession().userId
            //val tags
            val links = view?.findViewById<EditText>(R.id.textBox)
        }
    }

}