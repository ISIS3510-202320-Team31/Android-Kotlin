package com.example.hive.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R

class PartnerNamesAdapter : RecyclerView.Adapter<PartnerNamesAdapter.MListHolder>() {

    inner class MListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MListHolder {
        return MListHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_name_partner,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MListHolder, position: Int) {
        val name = differ.currentList[position]
        holder.itemView.apply {
            this.findViewById<android.widget.TextView>(R.id.namePartnerTextView).text = name
        }
    }

    fun submitList(list: List<String>) {
        differ.submitList(list)
    }

}