package com.example.hive.view

import SignUpFragment
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.hive.R
import com.example.hive.viewmodel.LoginViewModel
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.LoginViewModelProviderFactory

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val repository = UserRepository()
        val session = SessionManager(requireContext())
        val viewModelFactory = LoginViewModelProviderFactory(repository, session)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        val loginButton = view.findViewById<TextView>(R.id.buttonSignIn)

        viewModel._loginResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Inicio de sesión correctamente", Toast.LENGTH_SHORT).show()
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    val homeFragment = HomePageFragment()
                    transaction.replace(R.id.fragment_container, homeFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Inicio de sesión falló", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Logging in...", Toast.LENGTH_SHORT).show()
                }
            }
        })

        loginButton.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = view.findViewById<EditText>(R.id.editTextPassword).text.toString()

            val request = LoginRequest(username, password)

            // Trigger the login process in the ViewModel
            viewModel.loginVM(request)
        }

        // Obtiene una referencia al TextView
        val signUpTextView = view.findViewById<TextView>(R.id.textViewSignUpLink)

        signUpTextView.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val signUpFragment = SignUpFragment()
            transaction.replace(R.id.fragment_container, signUpFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
