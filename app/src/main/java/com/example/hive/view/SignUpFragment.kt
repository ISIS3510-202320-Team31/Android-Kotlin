import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hive.R
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import com.example.hive.view.LoginFragment
import com.example.hive.viewmodel.SignUpViewModel
import com.example.hive.viewmodel.SignUpViewModelProviderFactory
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker




class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var viewModelFactory: SignUpViewModelProviderFactory
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userRepository = UserRepository()
        viewModelFactory = SignUpViewModelProviderFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val buttonSignUp = view?.findViewById<Button>(R.id.buttonSignUp)

        buttonSignUp?.setOnClickListener {
            val name = view?.findViewById<EditText>(R.id.editTextName)?.text.toString()
            val username = view?.findViewById<EditText>(R.id.editTextUsername)?.text.toString()
            val email = view?.findViewById<EditText>(R.id.editTextEmail)?.text.toString()
            val password = view?.findViewById<EditText>(R.id.editTextPassword)?.text.toString()
            val confirmPassword = view?.findViewById<EditText>(R.id.editTextVerifyPassword)?.text.toString()
            val selectedCareer = view?.findViewById<Spinner>(R.id.spinnerCareer)?.selectedItem.toString()
            val birthdate = view?.findViewById<DatePicker>(R.id.datePickerBirthdate)

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
                Toast.makeText(requireContext(), "La contraseña debe ser igual a su confirmación", Toast.LENGTH_SHORT).show()
            }

            // Create a RegisterRequest object
            var registerRequest = try {
                RegisterRequest(username, name, selectedCareer, birthdateStr, email, password)
            } catch (e: Exception) {
                // Show toast message
                Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                // Show toast message
                Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
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
        viewModel.registerPage.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Registration was successful, go to the login page and print the response
                    println(resource.data)
                    Toast.makeText(requireContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, LoginFragment())
                    transaction.commit()
                }
                is Resource.Error -> {

                    if (resource.message == "Bad Request") {
                        // Show toast message
                        Toast.makeText(requireContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Show toast message
                        Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    // Show a loading indicator
                }
            }
        })
    }
}