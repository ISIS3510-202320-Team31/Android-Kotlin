package com.example.hive.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.UserCacheResponse
import com.example.hive.model.room.entities.User
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*

class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var elapsedTimeTextView: TextView
    private lateinit var viewModelUserProfileOffline: UserProfileOfflineViewModel
    private lateinit var user: UserSession
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var viewModelEventListOffline: EventListOfflineViewModel

    private var userParticipation: String = "0"
    private lateinit var userCache: UserCacheResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_user_profile, container, false)

        val userSession = SessionManager(requireContext())
        user = userSession.getUserSession()

        val viewModelFactoryOffline = context?.let{
            UserProfileOfflineViewModelProviderFactory(
                it
            )
        }

        viewModelUserProfileOffline =
            viewModelFactoryOffline?.let{
                ViewModelProvider(this,
                    it
                ).get(UserProfileOfflineViewModel::class.java)
            }!!

        // ViewModel offline for events
        val viewModelFactoryOfflineEvents = context?.let{
            EventListOfflineViewModelProviderFactory(
                it
            )
        }

        viewModelEventListOffline =
            viewModelFactoryOfflineEvents?.let{
                ViewModelProvider(this,
                    it
                ).get(EventListOfflineViewModel::class.java)
            }!!

        viewModelUserProfileOffline.allUsers?.observe(viewLifecycleOwner, Observer { resource ->
            val list = mutableListOf<UserCacheResponse>()
            resource.let {
                println("resource: $it")
                for (user in it){
                    val userToAdd = UserCacheResponse(
                        user.id,
                        user.name?:"",
                        user.email?:"",
                        user.participation?:"0",
                    )
                    list.add(userToAdd)
                }
                try {
                    userCache = list[0]

                    val nameTextView = view?.findViewById<TextView>(R.id.userName)
                    val emailTextView = view?.findViewById<TextView>(R.id.email)
                    val participationTextView = view?.findViewById<TextView>(R.id.eventsJoined)

                    if (nameTextView != null) {
                        nameTextView.text = userCache.name
                    }
                    if (emailTextView != null) {
                        emailTextView.text = userCache.email
                    }
                    if (participationTextView != null) {
                        participationTextView.text = userCache.participation
                    }

                }catch (e: Exception){
                    println("Exception: $e")
                    userCache = UserCacheResponse(
                        "",
                    "",
                    "",
                    "0")
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingProgressBar = view?.findViewById<ProgressBar>(R.id.loadingProgressBarProfile)
        connectionLiveData = ConnectionLiveData(requireContext())
        val swipeRefreshLayout: SwipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayoutProfile)!!
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = false

        elapsedTimeTextView = view?.findViewById<TextView>(R.id.timeSpent)!!

        connectionLiveData.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected){
                swipeRefreshLayout.isEnabled = true

                swipeRefreshLayout.setOnRefreshListener {
                    refreshFragment(swipeRefreshLayout)
                }
                sessionManager = SessionManager(requireContext())
                val viewModelFactory = sessionManager?.let{ UserProfileViewModelProviderFactory(it,requireContext()) }
                viewModel = ViewModelProvider(this, viewModelFactory!!).get(UserProfileViewModel::class.java)

                //Observer for time usage
                viewModel.elapsedTimeLiveData.observe(viewLifecycleOwner, Observer{ elapsedTime ->
                    handleTracking(elapsedTime)
                })


                //update user detail
                viewModel.userDetail.observe(viewLifecycleOwner, Observer { resourceUserDetail ->
                    val nameTextView = view?.findViewById<TextView>(R.id.userName)
                    val emailTextView = view?.findViewById<TextView>(R.id.email)
                    val participationTextView = view?.findViewById<TextView>(R.id.eventsJoined)
                    when (resourceUserDetail){
                        is Resource.Loading<*> -> {
                            println(resourceUserDetail)
                            if (nameTextView != null) {
                                nameTextView.text = "..."
                            }
                            if (emailTextView != null) {
                                emailTextView.text = "..."
                            }
                        }
                        is Resource.Success<*> -> {
                            viewModel.userParticipation.observe(viewLifecycleOwner, Observer { resource ->
                                val participationTextView = view?.findViewById<TextView>(R.id.eventsJoined)
                                when (resource){
                                    is Resource.Loading<*> -> {
                                        if (loadingProgressBar != null) {
                                            loadingProgressBar.visibility = View.VISIBLE
                                        }
                                        if (participationTextView != null) {
                                            participationTextView.text = "..."
                                        }
                                    }
                                    is Resource.Success<*> -> {

                                        if (loadingProgressBar != null) {
                                            loadingProgressBar.visibility = View.GONE
                                        }
                                        if (participationTextView != null) {
                                            participationTextView.text = resource.data?.size.toString()
                                            userParticipation = resource.data?.size.toString()
                                        }

                                        viewModelUserProfileOffline.removeUserDatabase()
                                        val userWeb = user.userId?.let { it1 ->
                                            User(
                                                it1,
                                                resourceUserDetail.data?.name,
                                                resourceUserDetail.data?.email,
                                                userParticipation
                                            )
                                        }
                                        if (userWeb != null) {
                                            if (userWeb.id != "") {
                                                viewModelUserProfileOffline.insertOneToDatabase(userWeb!!)
                                            }
                                        }


                                        if (userCache.name != resourceUserDetail.data?.name || userCache.email != resourceUserDetail.data?.email || userCache.participation != userParticipation){
                                            viewModelUserProfileOffline.removeUserDatabase()
                                            val user = user.userId?.let { it1 ->
                                                User(
                                                    it1,
                                                    resourceUserDetail.data?.name,
                                                    resourceUserDetail.data?.email,
                                                    userParticipation
                                                )
                                            }
                                            if (user != null) {
                                                viewModelUserProfileOffline.insertOneToDatabase(user)
                                            }

                                            if (nameTextView != null) {
                                                nameTextView.text = resourceUserDetail.data?.name
                                            }
                                            if (emailTextView != null) {
                                                emailTextView.text = resourceUserDetail.data?.email
                                            }
                                            if (participationTextView != null) {
                                                participationTextView.text = userParticipation
                                            }

                                        } else  {
                                            if (nameTextView != null) {
                                                nameTextView.text = userCache.name
                                            }
                                            if (emailTextView != null) {
                                                emailTextView.text = userCache.email
                                            }
                                            if (participationTextView != null) {
                                                participationTextView.text = userCache.participation
                                            }
                                        }
                                    }
                                    is Resource.Error<*> -> {
                                        if (loadingProgressBar != null) {
                                            loadingProgressBar.visibility = View.GONE
                                        }
                                        if (participationTextView != null) {
                                            participationTextView.text = ""
                                        }
                                    }
                                }
                            })


                        }
                        is Resource.Error<*> -> {
                            if (nameTextView != null) {
                                nameTextView.text = ""
                            }
                            if (emailTextView != null) {
                                emailTextView.text = ""
                            }
                        }
                    }
                })

                val buttonEstadisticas = view?.findViewById<Button>(R.id.EstadisticasButton)
                buttonEstadisticas?.setOnClickListener {
                    val activity = getActivity() as MainActivity
                    val fragment = EstadisticsFragment()
                    activity.replaceFragment(fragment)
                }

                val buttonSignOut = view?.findViewById<Button>(R.id.signOutButton)

                buttonSignOut?.setOnClickListener {
                    sessionManager = SessionManager(requireContext())
                    sessionManager.clearSession()
                    viewModelUserProfileOffline.removeUserDatabase()
                    viewModelEventListOffline.removeEventDatabase()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
                    startActivity(intent)
                    activity?.finish()
                }
            } else {
                swipeRefreshLayout.isEnabled = false
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun handleTracking(elapsedTimeSeconds: Long) {

        val formattedTime = when {
            elapsedTimeSeconds > 3599 -> {
                val hours = elapsedTimeSeconds / 3600
                val minutes = (elapsedTimeSeconds % 3600) / 60
                if (minutes <10 && hours <10){
                    "0$hours:0$minutes " + getString(R.string.profile_time_horas)
                }else if (minutes <10){
                    "$hours:0$minutes " + getString(R.string.profile_time_horas)
                }
                else if (hours <10){
                    "0$hours:$minutes " + getString(R.string.profile_time_horas)
                }
                else{
                    "$hours:$minutes " + getString(R.string.profile_time_horas)
                }
            }
            elapsedTimeSeconds > 59 -> {
                val minutes = elapsedTimeSeconds / 60
                val seconds = elapsedTimeSeconds % 60

                if (seconds <10 && minutes <10){
                    "0$minutes:0$seconds " + getString(R.string.profile_time_minutos)
                }else if (seconds <10){
                    "$minutes:0$seconds " + getString(R.string.profile_time_minutos)
                }
                else if (minutes <10){
                    "0$minutes:$seconds " + getString(R.string.profile_time_minutos)
                }
                else{
                    "$minutes:$seconds " + getString(R.string.profile_time_minutos)
                }
            }
            else -> {
                val seconds = elapsedTimeSeconds
                if (seconds <10){
                    "00:0$seconds " + getString(R.string.profile_time_segundos)
                }
                else{
                    "00:$seconds " + getString(R.string.profile_time_segundos)
                }
            }

        }
        elapsedTimeTextView.text = formattedTime
    }

    private fun refreshFragment( swipeRefreshLayout: SwipeRefreshLayout ) {
        swipeRefreshLayout.isRefreshing = true
        connectionLiveData.observe(viewLifecycleOwner, Observer { isAvailable ->
            if (isAvailable) {
                viewModel.getUserParticipationVM()
                viewModel.getUserDetailVM()
                viewModel.updateElapsedTime()

                viewModel.userParticipation.observe(viewLifecycleOwner, Observer { resource ->
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
