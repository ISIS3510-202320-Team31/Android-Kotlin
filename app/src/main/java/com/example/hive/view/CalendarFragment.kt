package com.example.hive.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
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
        val btnCreados = view.findViewById<android.widget.Button>(R.id.btnCreados)

        //Show ActivitiesFragment by default
        showFragment(CalendarActivitiesFragment())

        btnActivities.setOnClickListener {
            showFragment(CalendarActivitiesFragment())
            //Color BLUE
            btnActivities.setBackgroundColor(Color.parseColor("#2196F3"))
            //Color DARK GRAY
            btnHistorical.setBackgroundColor(Color.parseColor("#A2AEBB"))
            //Color DARK GRAY
            btnCreados.setBackgroundColor(Color.parseColor("#A2AEBB"))
        }

        btnHistorical.setOnClickListener {
            showFragment(CalendarHistoricalFragment())
            btnActivities.setBackgroundColor(Color.parseColor("#A2AEBB"))
            btnHistorical.setBackgroundColor(Color.parseColor("#2196F3"))
            btnCreados.setBackgroundColor(Color.parseColor("#A2AEBB"))
        }

        btnCreados.setOnClickListener {
            showFragment(CalendarCreadosFragment())
            btnActivities.setBackgroundColor(Color.parseColor("#A2AEBB"))
            btnHistorical.setBackgroundColor(Color.parseColor("#A2AEBB"))
            btnCreados.setBackgroundColor(Color.parseColor("#2196F3"))
        }

        return view
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_calendar_container, fragment)
        transaction.commit()
    }
}