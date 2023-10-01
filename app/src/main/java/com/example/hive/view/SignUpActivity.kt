package com.example.hive.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import com.example.hive.viewmodel.SignUpViewModel
import com.example.hive.viewmodel.SignUpViewModelProviderFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var viewModelFactory: SignUpViewModelProviderFactory
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up)

        userRepository = UserRepository()
        viewModelFactory = SignUpViewModelProviderFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)

        buttonSignUp?.setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextName)?.text.toString()
            val username = findViewById<EditText>(R.id.editTextUsername)?.text.toString()
            val email = findViewById<EditText>(R.id.editTextEmail)?.text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword)?.text.toString()
            val confirmPassword = findViewById<EditText>(R.id.editTextVerifyPassword)?.text.toString()
            val selectedCareer = findViewById<Spinner>(R.id.spinnerCareer)?.selectedItem.toString()
            val birthdate = findViewById<DatePicker>(R.id.datePickerBirthdate)

            val year = birthdate?.year
            val month = birthdate?.month?.plus(1)
            val dayOfMonth = birthdate?.dayOfMonth
            var birthdateStr = ""

            if (month != null) {
                if (month>9) {
                    var monthStr = month.toString()
                    birthdateStr = "$year-$monthStr-$dayOfMonth"
                }
                else {
                    var monthStr = "0$month"
                    birthdateStr = "$year-$monthStr-$dayOfMonth"
                }
            }


            if (password != confirmPassword) {
                // Show toast message
                Toast.makeText(this, "La contraseña debe ser igual a su confirmación", Toast.LENGTH_SHORT).show()
            }

            // Create a RegisterRequest object
            var registerRequest = try {
                RegisterRequest(username, name, selectedCareer, birthdateStr, email, password)
            } catch (e: Exception) {
                // Show toast message
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                null
            }

            // Check that none of the fields are empty and do not send the request if they are
            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedCareer.isEmpty() || birthdate == null) {
                // Show toast message
                registerRequest = null
            }

            // Call the registration method in the ViewModel
            if (registerRequest != null) {
                try {
                    viewModel.registerVM(registerRequest)
                } catch (e: Exception) {
                    // Show toast message
                    println(e.stackTrace)
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                // Show toast message
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                // Print all the fields
                println("Name: $name")
                println("Username: $username")
                println("Email: $email")
                println("Password: $password")
                println("Confirm Password: $confirmPassword")
                println("Career: $selectedCareer")
                println("Birthdate: $birthdateStr")
            }
        }

        // Observe the registerPage LiveData to handle the response
        viewModel.registerPage.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Registration was successful, go to the login page and print the response
                    println(resource.data)

                    // Start login activity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                is Resource.Error -> {

                    if (resource.message == "Bad Request") {
                        // Show toast message
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Show toast message
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    // Show a loading indicator
                }
            }
        })

        // Obtain a reference to the TextView
        val signUpTextView = findViewById<TextView>(R.id.textViewLoginLink)

        // Set the click listener
        signUpTextView?.setOnClickListener {
            // Start login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}