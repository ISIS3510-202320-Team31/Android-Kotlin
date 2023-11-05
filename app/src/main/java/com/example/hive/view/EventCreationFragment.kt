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
import com.example.hive.util.FormProgressCache
import com.example.hive.util.Resource
import com.example.hive.viewmodel.EventCreationViewModel
import com.example.hive.viewmodel.EventCreationViewModelProviderFactory
import java.sql.Date
import java.text.SimpleDateFormat

class EventCreationFragment : Fragment() {

    private lateinit var viewModel: EventCreationViewModel
    private lateinit var viewModelFactory: EventCreationViewModelProviderFactory
    companion object {
        val formProgressCache = FormProgressCache<String, FormData>(3)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = EventCreationViewModelProviderFactory()
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

            var createEventRequest = try {
                CreateEventRequest(name, place, formattedDate, description, num_participants.toInt(), category, state, duration.toInt(), creador, tags, links)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.eventCreationPage.observe(viewLifecycleOwner, Observer { resource ->
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
            formProgressCache.remove("formData")
            println("Borrado")
            println(formProgressCache.get("formData"))
        }
        else {

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
            val duration = view?.findViewById<EditText>(R.id.inputBoxDuration)?.text.toString()
            val tags = view?.findViewById<EditText>(R.id.textBox2)?.text.toString()
            val links = view?.findViewById<EditText>(R.id.textBox)?.text.toString()

            val formData = FormData(name, place, formattedDate, description, num_participants, category, duration, tags, links)
            formProgressCache.put("formData", formData)
            println("Pause")
            println(formProgressCache.get("formData"))
        }
    }

    override fun onResume() {
        super.onResume()

        val formData = formProgressCache.get("formData")
        println("Resume")
        println(formData)

        view?.findViewById<EditText>(R.id.inputBox)?.setText(formData?.name ?: "")
        view?.findViewById<EditText>(R.id.inputBoxLugar)?.setText(formData?.place ?: "")

        val calendarDate = view?.findViewById<CalendarView>(R.id.calendarView)
        if (formData != null) {
            val date = SimpleDateFormat("yyyy-MM-dd").parse(formData.formattedDate)
            calendarDate?.date = date.time
        }

        view?.findViewById<EditText>(R.id.textBoxDescription)?.setText(formData?.description ?: "")
        view?.findViewById<EditText>(R.id.inputBoxParticipant)?.setText(formData?.num_participants ?: "")

        val categorySpinner = view?.findViewById<Spinner>(R.id.spinner1)
        if (formData != null) {
            val categoryAdapter = categorySpinner?.adapter as ArrayAdapter<String>
            val categoryIndex = categoryAdapter.getPosition(formData.category)
            categorySpinner.setSelection(categoryIndex)
        }

        view?.findViewById<EditText>(R.id.inputBoxDuration)?.setText(formData?.duration ?: "")
        view?.findViewById<EditText>(R.id.textBox2)?.setText(formData?.tags ?: "")
        view?.findViewById<EditText>(R.id.textBox)?.setText(formData?.links ?: "")
    }

    private fun resourceHasSucceeded(): Boolean {
        val resource = viewModel.eventCreationPage.value
        return resource is Resource.Success
    }
}