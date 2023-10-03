package com.example.hive.view

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.UserProfileViewModel
import com.example.hive.viewmodel.UserProfileViewModelProviderFactory

class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var elapsedTimeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = UserRepository()
        sessionManager = SessionManager(requireContext())
        val viewModelFactory = sessionManager?.let{ UserProfileViewModelProviderFactory(repository,it) }

        elapsedTimeTextView = view?.findViewById<TextView>(R.id.timeSpent)!!

        viewModel = ViewModelProvider(this, viewModelFactory!!).get(UserProfileViewModel::class.java)

        //Observer for time usage
        viewModel.elapsedTimeLiveData.observe(viewLifecycleOwner, Observer{ elapsedTime ->
            handleTracking(elapsedTime)
        })

        //Update user participation
        viewModel.userParticipation.observe(viewLifecycleOwner, Observer { resource ->
            val participationTextView = view?.findViewById<TextView>(R.id.eventsJoined)

            when (resource){
                is Resource.Loading<*> -> {
                    if (participationTextView != null) {
                        participationTextView.text = "..."
                    }
                }
                is Resource.Success<*> -> {
                    if (participationTextView != null) {
                        participationTextView.text = resource.data?.size.toString()
                    }
                }
                is Resource.Error<*> -> {
                    if (participationTextView != null) {
                        participationTextView.text = ""
                    }
                }
            }


        })

        //update user detail
        viewModel.userDetail.observe(viewLifecycleOwner, Observer { resource ->
            val nameTextView = view?.findViewById<TextView>(R.id.userName)
            val emailTextView = view?.findViewById<TextView>(R.id.email)

            when (resource){
                is Resource.Loading<*> -> {
                    if (nameTextView != null) {
                        nameTextView.text = "..."
                    }
                    if (emailTextView != null) {
                        emailTextView.text = "..."
                    }
                }
                is Resource.Success<*> -> {
                    if (nameTextView != null) {
                        nameTextView.text = resource.data?.name
                    }
                    if (emailTextView != null) {
                        emailTextView.text = resource.data?.email
                    }
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

        viewModel.updateElapsedTime()

        val buttonSignOut = view?.findViewById<Button>(R.id.signOutButton)

        buttonSignOut?.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun handleTracking(elapsedTimeSeconds: Long) {

        val formattedTime = when {
            elapsedTimeSeconds > 3599 -> {
                val hours = elapsedTimeSeconds / 3600
                val minutes = (elapsedTimeSeconds % 3600) / 60
                if (minutes <10 && hours <10){
                    "0$hours:0$minutes horas"
                }else if (minutes <10){
                    "0$hours:$minutes horas"
                }
                else if (hours <10){
                    "$hours:0$minutes horas"
                }
                else{
                    "$hours:$minutes horas"
                }
            }
            elapsedTimeSeconds > 59 -> {
                val minutes = elapsedTimeSeconds / 60
                val seconds = elapsedTimeSeconds % 60

                if (seconds <10 && minutes <10){
                    "0$minutes:0$seconds minutos"
                }else if (seconds <10){
                    "0$minutes:$seconds minutos"
                }
                else if (minutes <10){
                    "$minutes:0$seconds minutos"
                }
                else{
                    "$minutes:$seconds minutos"
                }
            }
            else -> {
                val seconds = elapsedTimeSeconds
                if (seconds <10){
                    "00:0$seconds segundos"
                }
                else{
                    "00:$seconds segundos"
                }
            }

        }

        elapsedTimeTextView.text = formattedTime
    }

}