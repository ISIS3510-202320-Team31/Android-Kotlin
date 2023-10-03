package com.example.hive.view

import android.content.Intent
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
import com.example.hive.viewmodel.UserProfileViewModel

class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        val buttonSignOut = view?.findViewById<Button>(R.id.signOutButton)

        //Time tracker
        handdleTracking()

        buttonSignOut?.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        handdleTracking()
    }


    private fun handdleTracking() {

        val sessionManager = SessionManager(requireContext())
        val elapsedTimeSeconds = sessionManager.getElapsedTime()

        val elapsedTimeTextView = view?.findViewById<TextView>(R.id.timeSpent)

        //Si hay más de 59 segundos dar el timepo en minutos
        if (elapsedTimeSeconds > 59) {
            val elapsedTimeMinutes = elapsedTimeSeconds / 60
            val elapsedTimeSeconds = elapsedTimeSeconds % 60

            elapsedTimeTextView?.text = "$elapsedTimeMinutes:$elapsedTimeSeconds minutos"
        }
        // si hay más de 59minutos y 59 segundos dar en horas
        else if (elapsedTimeSeconds > 3599) {
            val elapsedTimeHours = elapsedTimeSeconds / 3600
            val elapsedTimeMinutes = (elapsedTimeSeconds % 3600) / 60
            val elapsedTimeSeconds = elapsedTimeSeconds % 60
            elapsedTimeTextView?.text = "$elapsedTimeHours:$elapsedTimeMinutes horas"
        }
        else {
            elapsedTimeTextView?.text = "$elapsedTimeSeconds segundos"
        }
    }

}