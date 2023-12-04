package com.example.hive.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.network.responses.TopCreatorsResponse
import com.example.hive.util.ConnectionLiveData
import com.example.hive.viewmodel.TopCreatorsViewModel

class TopCreatorsAdapter(private val lifecycleOwner: LifecycleOwner, private val context: Context) : RecyclerView.Adapter<TopCreatorsAdapter.MListHolder>() {

    inner class MListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<TopCreatorsResponse>() {
        override fun areItemsTheSame(oldItem: TopCreatorsResponse, newItem: TopCreatorsResponse): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: TopCreatorsResponse, newItem: TopCreatorsResponse): Boolean {
            return oldItem == newItem
        }
    }

    private lateinit var topCreatorsViewModel: TopCreatorsViewModel
    private lateinit var connectionLiveData: ConnectionLiveData

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MListHolder {
        return MListHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_name_creator,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MListHolder, position: Int) {
        val topCreator = differ.currentList[position]
        val creatorView = holder.itemView.findViewById<TextView>(R.id.nameCreatorTextView)

        holder.itemView.apply {
            val position_plus_one = position + 1
            if (context.getString(R.string.top_creators_error) == topCreator.name)
                creatorView.text = topCreator.name
            else
                creatorView.text = position_plus_one.toString() + ". " + topCreator.name
        }

    }

    fun submitList(list: List<TopCreatorsResponse>) {
        differ.submitList(list)
    }
}