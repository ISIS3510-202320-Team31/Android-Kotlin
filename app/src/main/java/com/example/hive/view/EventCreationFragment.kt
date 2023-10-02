package com.example.hive.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hive.R
import com.example.hive.viewmodel.EventCreationViewModel

class EventCreationFragment : Fragment() {

    companion object {
        fun newInstance() = EventCreationFragment()
    }

    private lateinit var viewModel: EventCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_creation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventCreationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}