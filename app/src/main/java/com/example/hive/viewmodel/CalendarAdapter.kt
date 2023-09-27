package com.example.hive.viewmodel

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.hive.R

class CalendarAdapter(private val daysList: List<String>):
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dayText: TextView = itemView.findViewById(R.id.day_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = daysList[position]
        holder.dayText.text = day
    }

    override fun getItemCount(): Int {
        return daysList.size
    }



}