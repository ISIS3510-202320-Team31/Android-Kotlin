package com.example.hive.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

        viewModelFactory = SignUpViewModelProviderFactory()
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

            // Check that no field is empty and show error
            if (name.isEmpty()) {
                findViewById<EditText>(R.id.editTextName).error = "Por favor ingrese su nombre"
                findViewById<EditText>(R.id.editTextName).requestFocus()
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                findViewById<EditText>(R.id.editTextUsername).error = "Por favor ingrese su usuario"
                findViewById<EditText>(R.id.editTextUsername).requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                findViewById<EditText>(R.id.editTextEmail).error = "Por favor ingrese su correo electrónico"
                findViewById<EditText>(R.id.editTextEmail).requestFocus()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                findViewById<EditText>(R.id.editTextEmail).error = "Por favor ingrese un correo electrónico válido"
                findViewById<EditText>(R.id.editTextEmail).requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                findViewById<EditText>(R.id.editTextPassword).error = "Por favor ingrese su contraseña"
                findViewById<EditText>(R.id.editTextPassword).requestFocus()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                findViewById<EditText>(R.id.editTextVerifyPassword).error = "Por favor ingrese su contraseña"
                findViewById<EditText>(R.id.editTextVerifyPassword).requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                // Show toast message
                Toast.makeText(this, "La contraseña debe ser igual a su confirmación", Toast.LENGTH_SHORT).show()
            }
            //Check that the username is at least 4 characters long
            if (username.length < 4) {
                findViewById<EditText>(R.id.editTextUsername).error = "El usuario debe tener al menos 4 caracteres"
                findViewById<EditText>(R.id.editTextUsername).requestFocus()
                return@setOnClickListener
            }

            // Check that the password should have at least one uppercase letter, 1 lowercase letter, 1 number, and 1 special character
            val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.])[A-Za-z\\d@\$!%.*?&]{8,}\$")
            if (!regex.matches(password)) {
                findViewById<EditText>(R.id.editTextPassword).error = "La contraseña debe tener al menos 1 mayúscula, 1 número y 1 carácter especial y al menos 8 caracteres"
                findViewById<EditText>(R.id.editTextPassword).requestFocus()
                return@setOnClickListener
            }

            // Create a RegisterRequest object
            var registerRequest = try {
                RegisterRequest(username, name, selectedCareer, birthdateStr, email, password)
            } catch (e: Exception) {
                // Show toast message
                Toast.makeText(this, "Error al registrar usuario, revise los datos ingresados", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Error al registrar usuario, revise los datos ingresados", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "Error al registrar usuario, ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {

                    if (resource.message == "Bad Request") {
                        // Show toast message
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Show toast message
                        Toast.makeText(this, "Error al registrar usuario, revise los datos ingresados", Toast.LENGTH_SHORT).show()
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