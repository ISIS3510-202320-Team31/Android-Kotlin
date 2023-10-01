package com.example.hive.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.hive.R

class CalendarFragment : Fragment() {

    private lateinit var frameLayout: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        frameLayout = view.findViewById(R.id.fragment_calendar_container)

        val btnActivities = view.findViewById<android.widget.Button>(R.id.btnActivities)
        val btnHistorical = view.findViewById<android.widget.Button>(R.id.btnHistorical)

        btnActivities.setOnClickListener {
            showFragment(CalendarActivitiesFragment())
        }

        btnHistorical.setOnClickListener {
            showFragment(CalendarHistoricalFragment())
        }

        //Show ActivitiesFragment by default
        showFragment(CalendarActivitiesFragment())

        return view
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_calendar_container, fragment)
        transaction.commit()
    }
}