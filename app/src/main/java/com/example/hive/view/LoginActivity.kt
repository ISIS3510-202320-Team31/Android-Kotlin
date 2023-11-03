package com.example.hive.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.LoginViewModel
import com.example.hive.viewmodel.LoginViewModelProviderFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val repository = UserRepository()
        val session = SessionManager(this)
        val viewModelFactory = LoginViewModelProviderFactory(repository, session)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        val loginButton = findViewById<TextView>(R.id.buttonSignIn)

        viewModel._loginResult.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Toast.makeText(this, getString(R.string.login_loading), Toast.LENGTH_SHORT).show()
                }
            }
        })

        loginButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (username.isEmpty()) {
                findViewById<EditText>(R.id.editTextEmail).error = getString(R.string.login_error_username)
                findViewById<EditText>(R.id.editTextEmail).requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                findViewById<EditText>(R.id.editTextPassword).error = getString(R.string.login_error_password)
                findViewById<EditText>(R.id.editTextPassword).requestFocus()
                return@setOnClickListener
            }


            val request = LoginRequest(username, password)

            // Trigger the login process in the ViewModel
            viewModel.loginVM(request)
        }

        // Obtain a reference to the TextView
        val signUpTextView = findViewById<TextView>(R.id.textViewSignUpLink)

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}