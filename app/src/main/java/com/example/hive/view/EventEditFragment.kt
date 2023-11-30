package com.example.hive.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.requests.EditEventRequest
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.FormProgressCache
import com.example.hive.util.Resource
import com.example.hive.viewmodel.EventEditViewModel
import com.example.hive.viewmodel.EventEditViewModelProviderFactory

class EventEditFragment(
    eventIDTextView: TextView,
    eventNameTextView: TextView,
    eventCategoryTextView: TextView,
    eventDateTextView: TextView,
    eventDuracionTextView: TextView,
    eventDescriptionTextView: TextView,
    eventLugarTextView: TextView,
    eventParticipantTextView: TextView,
) : Fragment() {

    private lateinit var viewModel: EventEditViewModel
    private lateinit var viewModelFactory: EventEditViewModelProviderFactory
    private lateinit var connectionLiveData: ConnectionLiveData
    companion object {
        val formProgressCache = FormProgressCache<String, FormData>(4)
    }

    data class FormData(val name: String,
                            val place: String,
                            val formattedDate: String,
                            val description: String,
                            val num_participants: String,
                            val category: String,
                            val duration: String,
                            val tags: String,
                            val links: String)

    // crate variables with the data of the event
    private var eventIDTextView = eventIDTextView
    private var eventNameTextView = eventNameTextView
    private var eventCategoryTextView = eventCategoryTextView
    private var eventDateTextView = eventDateTextView
    private var eventDuracionTextView = eventDuracionTextView
    private var eventDescriptionTextView = eventDescriptionTextView
    private var eventLugarTextView = eventLugarTextView
    private var eventNumParticipantsTextView = eventParticipantTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = context?.let { EventEditViewModelProviderFactory(it) }!!
        viewModel = ViewModelProvider(this, viewModelFactory).get(EventEditViewModel::class.java)
        connectionLiveData = ConnectionLiveData(requireContext())

        Log.d("ID", eventIDTextView.text.toString())
        Log.d("NAME", eventNameTextView.text.toString())
        Log.d("CATEGORY", eventCategoryTextView.text.toString())
        Log.d("DATE", eventDateTextView.text.toString())
        Log.d("DURATION", eventDuracionTextView.text.toString())
        Log.d("DESCRIPTION", eventDescriptionTextView.text.toString())
        Log.d("PLACE", eventLugarTextView.text.toString())
        Log.d("PARTICIPANTS", eventNumParticipantsTextView.text.toString())

        return inflater.inflate(R.layout.fragment_event_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val session = SessionManager(requireContext())
        val buttonCreateEvent = view?.findViewById<Button>(R.id.submitButton)
        buttonCreateEvent?.isEnabled = false
        buttonCreateEvent?.setBackgroundColor(Color.parseColor("#A2AEBB"))
        connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                buttonCreateEvent?.isEnabled = true
                buttonCreateEvent?.setBackgroundColor(Color.parseColor("#2196F3"))
                buttonCreateEvent?.setOnClickListener {
                    val name = view?.findViewById<EditText>(R.id.inputBox)?.text.toString()
                    val place = view?.findViewById<EditText>(R.id.inputBoxLugar)?.text.toString()
                    val calendarDate = view?.findViewById<DatePicker>(R.id.datePicker)
                    val year = calendarDate?.year
                    val month = calendarDate?.month?.plus(1)
                    val dayOfMonth = calendarDate?.dayOfMonth
                    var formattedDate = ""

                    if (month != null) {
                        if (month>9) {
                            var monthStr = month.toString()
                            formattedDate = "$year-$monthStr-$dayOfMonth"
                        }
                        else {
                            var monthStr = "0$month"
                            formattedDate = "$year-$monthStr-$dayOfMonth"
                        }
                    }
                    Log.d("date", "FECHA")
                    Log.d("date", formattedDate)
                    val description = view?.findViewById<EditText>(R.id.textBoxDescription)?.text.toString()
                    val num_participants = view?.findViewById<EditText>(R.id.inputBoxParticipant)?.text.toString()
                    var category = view?.findViewById<Spinner>(R.id.spinner1)?.selectedItem.toString()
                    if (category == "Académico") {
                        category = "ACADEMIC"
                    }
                    else if (category == "Deportivo") {
                        category = "SPORTS"
                    }
                    else if (category == "Cultural") {
                        category = "CULTURAL"
                    }
                    else if (category == "Entretenimiento") {
                        category = "ENTERTAINMENT"
                    }
                    else if (category == "Otros") {
                        category = "OTHER"
                    }
                    val state = true
                    val duration = view?.findViewById<EditText>(R.id.inputBoxDuration)?.text.toString()
                    val creador = session.getUserSession().userId.toString()
                    val tags = view?.findViewById<EditText>(R.id.textBox2)?.text.toString().split(" ")
                    val links = view?.findViewById<EditText>(R.id.textBox)?.text.toString().split(" ")

                    if (name.isEmpty()) {
                        view?.findViewById<EditText>(R.id.inputBox)?.error = requireContext().getString(R.string.error_nombre_evento)
                        view?.findViewById<EditText>(R.id.inputBox)?.requestFocus()
                        return@setOnClickListener
                    }
                    if (place.isEmpty()) {
                        view?.findViewById<EditText>(R.id.inputBoxLugar)?.error = requireContext().getString(R.string.error_lugar_evento)
                        view?.findViewById<EditText>(R.id.inputBoxLugar)?.requestFocus()
                        return@setOnClickListener
                    }
                    if (description.isEmpty()) {
                        view?.findViewById<EditText>(R.id.textBoxDescription)?.error = requireContext().getString(R.string.error_descripcion_evento)
                        view?.findViewById<EditText>(R.id.textBoxDescription)?.requestFocus()
                        return@setOnClickListener
                    }
                    if (num_participants.isEmpty()) {
                        view?.findViewById<EditText>(R.id.inputBoxParticipant)?.error = requireContext().getString(R.string.error_participantes_evento)
                        view?.findViewById<EditText>(R.id.inputBoxParticipant)?.requestFocus()
                        return@setOnClickListener
                    }
                    if (duration.isEmpty()) {
                        view?.findViewById<EditText>(R.id.inputBoxDuration)?.error = requireContext().getString(R.string.error_duracion_evento)
                        view?.findViewById<EditText>(R.id.inputBoxDuration)?.requestFocus()
                        return@setOnClickListener
                    }
                    if (links.all { it.isNotBlank() }){
                        for (link in links) {
                            if (!android.util.Patterns.WEB_URL.matcher(link).matches()) {
                                view?.findViewById<EditText>(R.id.textBox)?.error = requireContext().getString(R.string.error_enlaces_evento)
                                view?.findViewById<EditText>(R.id.textBox)?.requestFocus()
                                return@setOnClickListener
                            }
                        }
                    }

                    var editEventRequest = try {
                        EditEventRequest(name, place, formattedDate, description, num_participants.toInt(), category, state, duration.toInt(), creador, tags, links)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
                        null
                    }

                    if (name.isEmpty() || place.isEmpty() || formattedDate.isEmpty() || description.isEmpty() || num_participants.isEmpty() || category.isEmpty() || duration.isEmpty() || creador.isEmpty()) {
                        // Show toast message
                        editEventRequest = null
                    }

                    if (editEventRequest != null) {
                        try {
                            viewModel.editEventVM(eventIDTextView.text.toString(), editEventRequest)
                        } catch (e: Exception) {
                            // Show toast message
                            println(e.stackTrace)
                            Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                buttonCreateEvent?.isEnabled = false
            }
            setupForm()
        })

        viewModel.eventEditPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    println(resource.data)
                    Toast.makeText(requireContext(), requireContext().getString(R.string.evento_registrado), Toast.LENGTH_SHORT).show()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, HomePageFragment())
                    transaction.commit()
                }
                is Resource.Error -> {

                    if (resource.message == "Bad Request") {
                        // Show toast message
                        Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_bad_request), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Show toast message
                        Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_bad_request), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    // Show a loading indicator
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        if (resourceHasSucceeded()) {
            formProgressCache.remove(eventIDTextView.text.toString())
            println("Borrado")
            println(formProgressCache.get(eventIDTextView.text.toString()))
        }
        else {

            val name = view?.findViewById<EditText>(R.id.inputBox)?.text.toString()
            val place = view?.findViewById<EditText>(R.id.inputBoxLugar)?.text.toString()
            val calendarDate = view?.findViewById<DatePicker>(R.id.datePicker)
            val year = calendarDate?.year
            val month = calendarDate?.month?.plus(1)
            val dayOfMonth = calendarDate?.dayOfMonth
            var formattedDate = ""

            if (month != null) {
                if (month>9) {
                    var monthStr = month.toString()
                    formattedDate = "$year-$monthStr-$dayOfMonth"
                }
                else {
                    var monthStr = "0$month"
                    formattedDate = "$year-$monthStr-$dayOfMonth"
                }
            }
            val description = view?.findViewById<EditText>(R.id.textBoxDescription)?.text.toString()
            val num_participants = view?.findViewById<EditText>(R.id.inputBoxParticipant)?.text.toString()
            val category = view?.findViewById<Spinner>(R.id.spinner1)?.selectedItem.toString()
            val duration = view?.findViewById<EditText>(R.id.inputBoxDuration)?.text.toString()
            val tags = view?.findViewById<EditText>(R.id.textBox2)?.text.toString()
            val links = view?.findViewById<EditText>(R.id.textBox)?.text.toString()

            val formData = FormData(name, place, formattedDate, description, num_participants, category, duration, tags, links)
            formProgressCache.put(eventIDTextView.text.toString(), formData)
        }
    }

    override fun onResume() {
        super.onResume()
        setupForm()
    }

    private fun setupForm() {

        val formData = formProgressCache.get(eventIDTextView.text.toString())
        if (formData != null) {

            view?.findViewById<EditText>(R.id.inputBox)?.setText(formData?.name ?: "")
            view?.findViewById<EditText>(R.id.inputBoxLugar)?.setText(formData?.place ?: "")

            if (formData != null) {
                val dateParts = formData.formattedDate.split("-")
                if (dateParts.size == 3) {
                    val year = dateParts[0].toInt()
                    val month = dateParts[1].toInt() - 1
                    val day = dateParts[2].toInt()
                    val datePicker = view?.findViewById<DatePicker>(R.id.datePicker)
                    datePicker?.init(year, month, day, null)
                }
            }


            view?.findViewById<EditText>(R.id.textBoxDescription)?.setText(formData?.description ?: "")
            view?.findViewById<EditText>(R.id.inputBoxParticipant)?.setText(formData?.num_participants ?: "")

            val categorySpinner = view?.findViewById<Spinner>(R.id.spinner1)
            if (formData != null) {
                val categoryAdapter = categorySpinner?.adapter as ArrayAdapter<String>
                var category = formData.category
                if (category == "ACADEMIC") {
                    category = "Académico"
                }
                else if (category == "SPORTS") {
                    category = "Deportivo"
                }
                else if (category == "CULTURAL") {
                    category = "Cultural"
                }
                else if (category == "ENTERTAINMENT") {
                    category = "Entretenimiento"
                }
                else if (category == "OTHER") {
                    category = "Otros"
                }
                val categoryIndex = categoryAdapter.getPosition(category)
                categorySpinner.setSelection(categoryIndex)
            }

            view?.findViewById<EditText>(R.id.inputBoxDuration)?.setText(formData?.duration ?: "")
            view?.findViewById<EditText>(R.id.textBox2)?.setText(formData?.tags ?: "")
            view?.findViewById<EditText>(R.id.textBox)?.setText(formData?.links ?: "")

        } else {

            if (eventNameTextView != null) {
                view?.findViewById<EditText>(R.id.inputBox)?.setText(eventNameTextView.text.toString())
            }
            val categorySpinner = view?.findViewById<Spinner>(R.id.spinner1)
            if (eventCategoryTextView != null) {
                val categoryAdapter = categorySpinner?.adapter as ArrayAdapter<String>
                var category = eventCategoryTextView.text.toString()
                if (category == "ACADEMIC") {
                    category = "Académico"
                }
                else if (category == "SPORTS") {
                    category = "Deportivo"
                }
                else if (category == "CULTURAL") {
                    category = "Cultural"
                }
                else if (category == "ENTERTAINMENT") {
                    category = "Entretenimiento"
                }
                else if (category == "OTHER") {
                    category = "Otros"
                }
                val categoryIndex = categoryAdapter.getPosition(category)
                categorySpinner.setSelection(categoryIndex)
            }
            if (eventDateTextView != null) {
                val dateParts = eventDateTextView.text.toString().split("/")
                if (dateParts.size == 3) {
                    val day = dateParts[0].toInt()
                    val month = dateParts[1].toInt() - 1
                    val year = dateParts[2].toInt()
                    val datePicker = view?.findViewById<DatePicker>(R.id.datePicker)
                    datePicker?.init(year, month, day, null)
                }
            }
            if (eventDuracionTextView != null) {
                val durationText = eventDuracionTextView.text.toString().split(" ")[0]  // Obtén solo el número de minutos
                view?.findViewById<EditText>(R.id.inputBoxDuration)?.setText(durationText)
            }
            if (eventDescriptionTextView != null) {
                view?.findViewById<EditText>(R.id.textBoxDescription)?.setText(eventDescriptionTextView.text.toString())
            }
            if (eventLugarTextView != null) {
                view?.findViewById<EditText>(R.id.inputBoxLugar)?.setText(eventLugarTextView.text.toString())
            }
            if (eventNumParticipantsTextView != null) {
                val participantsText = eventNumParticipantsTextView.text.toString().split(" ")[2]
                view?.findViewById<EditText>(R.id.inputBoxParticipant)?.setText(participantsText)
            }

        }
    }

    private fun resourceHasSucceeded(): Boolean {
        val resource = viewModel.eventEditPage.value
        return resource is Resource.Success
    }
}