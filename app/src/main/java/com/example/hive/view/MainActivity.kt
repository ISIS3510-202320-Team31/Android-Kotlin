package com.example.hive.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import androidx.fragment.app.Fragment
import com.example.hive.R
import com.example.hive.databinding.ActivityMainBinding
import com.example.hive.model.adapters.SessionManager
import com.example.hive.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (!isUserLoggedIn()) {
            navigateToLogin()
        }

        replaceFragment(HomePageFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.ic_home -> {
                    replaceFragment(HomePageFragment())
                    true
                }
                R.id.ic_create -> {
                    replaceFragment(EventCreationFragment())
                    true
                }
                R.id.ic_calendar -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.ic_profile -> {
                    replaceFragment(UserProfileFragment())
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        // Check if the user is logged in using SessionManager
        val userSession = sessionManager.getUserSession()
        return userSession.authToken != null && userSession.userId != null
    }

    private fun navigateToLogin() {
        // Redirect to the LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}