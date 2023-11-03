package com.example.hive.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.requests.CreateEventRequest
import com.example.hive.util.Resource
import com.example.hive.viewmodel.EventCreationViewModel
import com.example.hive.viewmodel.EventCreationViewModelProviderFactory
import java.sql.Date
import java.text.SimpleDateFormat

class EventCreationFragment : Fragment() {

    private lateinit var viewModel: EventCreationViewModel
    private lateinit var viewModelFactory: EventCreationViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = context?.let { EventCreationViewModelProviderFactory(it) }!!
        viewModel = ViewModelProvider(this, viewModelFactory).get(EventCreationViewModel::class.java)
        return inflater.inflate(R.layout.fragment_event_creation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val session = SessionManager(requireContext())
        val buttonCreateEvent = view?.findViewById<Button>(R.id.submitButton)
        buttonCreateEvent?.setOnClickListener{
            val name = view?.findViewById<EditText>(R.id.inputBox)?.text.toString()
            val place = view?.findViewById<EditText>(R.id.inputBoxLugar)?.text.toString()
            val calendarDate = view?.findViewById<CalendarView>(R.id.calendarView)
            val dateMillis = calendarDate?.date ?: 0
            val date = Date(dateMillis)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = sdf.format(date)
            val description = view?.findViewById<EditText>(R.id.textBoxDescription)?.text.toString()
            val num_participants = view?.findViewById<EditText>(R.id.inputBoxParticipant)?.text.toString()
            val category = view?.findViewById<Spinner>(R.id.spinner1)?.selectedItem.toString()
            val state = true
            val duration = view?.findViewById<EditText>(R.id.inputBoxDuration)?.text.toString()
            val creador = session.getUserSession().userId.toString()
            val tags = view?.findViewById<EditText>(R.id.textBox2)?.text.toString().split(" ")
            val links = view?.findViewById<EditText>(R.id.textBox)?.text.toString().split(" ")

            if (name.isEmpty()) {
                view?.findViewById<EditText>(R.id.inputBox)?.error = "Por favor ingrese su nombre"
                view?.findViewById<EditText>(R.id.inputBox)?.requestFocus()
                return@setOnClickListener
            }
            if (place.isEmpty()) {
                view?.findViewById<EditText>(R.id.inputBoxLugar)?.error = "Por favor ingrese el lugar del evento"
                view?.findViewById<EditText>(R.id.inputBoxLugar)?.requestFocus()
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                view?.findViewById<EditText>(R.id.textBoxDescription)?.error = "Por favor ingrese la descripcion del evento"
                view?.findViewById<EditText>(R.id.textBoxDescription)?.requestFocus()
                return@setOnClickListener
            }
            if (num_participants.isEmpty()) {
                view?.findViewById<EditText>(R.id.inputBoxParticipant)?.error = "Por favor la cantidad de participantes"
                view?.findViewById<EditText>(R.id.inputBoxParticipant)?.requestFocus()
                return@setOnClickListener
            }
            if (duration.isEmpty()) {
                view?.findViewById<EditText>(R.id.inputBoxDuration)?.error = "Ingrese la duracion del evento"
                view?.findViewById<EditText>(R.id.inputBoxDuration)?.requestFocus()
                return@setOnClickListener
            }
            if (links.all { it.isNotBlank() }){
                for (link in links) {
                    if (!android.util.Patterns.WEB_URL.matcher(link).matches()) {
                        view?.findViewById<EditText>(R.id.textBox)?.error = "Por favor ingrese correctamente los links del evento"
                        view?.findViewById<EditText>(R.id.textBox)?.requestFocus()
                        return@setOnClickListener
                    }
                }
            }
            var createEventRequest = try {
                CreateEventRequest(name, place, formattedDate, description, num_participants.toInt(), category, state, duration.toInt(), creador, tags, links)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al registrar el evento", Toast.LENGTH_SHORT).show()
                null
            }

            if (name.isEmpty() || place.isEmpty() || formattedDate.isEmpty() || description.isEmpty() || num_participants.isEmpty() || category.isEmpty() || duration.isEmpty() || creador.isEmpty()) {
                // Show toast message
                createEventRequest = null
            }

            if (createEventRequest != null) {
                try {
                    viewModel.createEventVM(createEventRequest)
                } catch (e: Exception) {
                    // Show toast message
                    println(e.stackTrace)
                    Toast.makeText(requireContext(), "Error al registrar el evento", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireContext(), "Error al registrar el evento", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.eventCreationPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    println(resource.data)
                    Toast.makeText(requireContext(), "El evento ha sido registrado exitosamente", Toast.LENGTH_SHORT).show()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, HomePageFragment())
                    transaction.commit()
                }
                is Resource.Error -> {

                    if (resource.message == "Bad Request") {
                        // Show toast message
                        Toast.makeText(requireContext(), "El evento no se ha registrado correctamente, por favor revisa que todos los campos esten diligenciados correctamente", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Show toast message
                        Toast.makeText(requireContext(), "El evento no se ha registrado correctamente, por favor revisa que todos los campos esten diligenciados correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    // Show a loading indicator
                }
            }
        })
    }

}