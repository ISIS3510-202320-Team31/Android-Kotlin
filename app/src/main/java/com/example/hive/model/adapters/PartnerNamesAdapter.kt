package com.example.hive.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R

class PartnerNamesAdapter : RecyclerView.Adapter<PartnerNamesAdapter.ViewHolder>() {

    private var names: List<String> = listOf()

    fun submitList(names: List<String>) {
        this.names = names
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name_partner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(names[position])
    }

    override fun getItemCount(): Int = names.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.findViewById<TextView>(R.id.namePartnerTextView).text = name
        }
    }
}