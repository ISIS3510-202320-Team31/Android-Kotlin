package com.example.hive.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hive.R
import com.example.hive.model.adapters.EventsAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.adapters.TopCreatorsAdapter
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.network.responses.TopCreatorsResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.model.room.entities.Event
import com.example.hive.model.room.entities.TopCreators
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TopCreatorsFragment : Fragment() {

    private lateinit var viewModelTopCreators : TopCreatorsViewModel
    private lateinit var topCreatorsAdapter: TopCreatorsAdapter
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var viewModelTopCreatorsOffline: TopCreatorsOfflineViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_top_creators, container, false)
        val swipeRefreshLayout: SwipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayoutTopCreators)!!
        swipeRefreshLayout.isRefreshing = false;
        swipeRefreshLayout.isEnabled = false;

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTopCreators)
        topCreatorsAdapter = context?.let {
            TopCreatorsAdapter(this,
                it
            )
        }!!
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = topCreatorsAdapter

        // Set up ViewModel
        val viewModelFactoryOffline = context?.let {
            TopCreatorsOfflineViewModelProviderFactory(
                it
            )
        }

        viewModelTopCreatorsOffline =
            viewModelFactoryOffline?.let {
                ViewModelProvider(this,
                    it
                ).get(TopCreatorsOfflineViewModel::class.java)
            }!!

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBarTopCreators)

        viewModelTopCreatorsOffline.allTopCreators?.observe(viewLifecycleOwner, Observer { resource ->
            loadingProgressBar.visibility = View.GONE
            val list = mutableListOf<TopCreatorsResponse>()
            resource.let {

                // Cast manually the list of events to the list of EventResponse
                for (topCreator in it) {
                    val topCreatorToAdd = TopCreatorsResponse(
                        topCreator.name ?: "",
                        (topCreator.average ?: 0.0) as Float,
                    )
                    list.add(topCreatorToAdd)
                }

                if (list.size > 0) {
                    topCreatorsAdapter.submitList(list)
                }
                else {
                    // Create a list of 1 string to display the "No events" message
                    val list = List<TopCreatorsResponse>(1) { i ->
                        TopCreatorsResponse(
                            getString(R.string.top_creators_error),
                            0.0f,
                        )
                    }
                    topCreatorsAdapter.submitList(list)
                }
            }
        })
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val loadingProgressBar = view?.findViewById<ProgressBar>(R.id.loadingProgressBarTopCreators)
        connectionLiveData = ConnectionLiveData(requireContext())
        val swipeRefreshLayout: SwipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayoutTopCreators)!!
        connectionLiveData.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                // Refresh fragment
                swipeRefreshLayout.isEnabled = true

                swipeRefreshLayout.setOnRefreshListener {
                    refreshFragment(swipeRefreshLayout)
                }
                val viewModelFactory = context?.let { TopCreatorsViewModelProviderFactory(it) }
                viewModelTopCreators = viewModelFactory?.let {
                    ViewModelProvider(this,
                        it
                    ).get(TopCreatorsViewModel::class.java)
                }!!

                viewModelTopCreators.topCreators.observe(viewLifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Loading<*> -> {
                            // Show progress bar
                            if (loadingProgressBar != null) {
                                loadingProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Success<*> -> {
                            if (loadingProgressBar != null) {
                                loadingProgressBar.visibility = View.GONE
                            }
                            // Update the RecyclerView with the list of events
                            resource.data?.let {
                                // Filter the list for only the ones with date of today and after

                                topCreatorsAdapter.submitList(it) }

                            viewModelTopCreatorsOffline.removeTopCreatorsDatabase()

                            // Loop over the filtered list and create an EventEntity for each event
                            val list = List<TopCreators>(resource.data?.size ?: 0) { i ->
                                TopCreators(
                                    resource.data?.get(i)?.name ?: "",
                                    resource.data?.get(i)?.average ?: 0.0f,
                                )
                            }
                            viewModelTopCreatorsOffline.insertToDatabase(list)
                        }
                        is Resource.Error<*> -> {
                            // Handle error state (e.g., show an error message)
                        }
                    }
                })

            } else {
                swipeRefreshLayout.isEnabled = false
            }
        })
    }


    private fun refreshFragment( swipeRefreshLayout: SwipeRefreshLayout ) {
        swipeRefreshLayout.isRefreshing = true
        connectionLiveData.observe(viewLifecycleOwner, Observer { isAvailable ->
            if (isAvailable) {
                viewModelTopCreators.getTopCreators()
                viewModelTopCreators.topCreators.observe(viewLifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Loading<*> -> {
                            // loading indicator will be kept
                        }
                        is Resource.Success<*> -> {
                            // Stop the loading indicator once the data has been loaded
                            swipeRefreshLayout.isRefreshing = false
                        }
                        is Resource.Error<*> -> {
                            // Stop the loading indicator in case of error
                            swipeRefreshLayout.isRefreshing = false
                            // Manage the error state (e.g., show an error message)
                            Toast.makeText(requireContext(), getString(R.string.swipe_down_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        })
    }

}