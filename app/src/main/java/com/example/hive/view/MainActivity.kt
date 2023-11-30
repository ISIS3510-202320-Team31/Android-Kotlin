package com.example.hive.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hive.R
import com.example.hive.databinding.ActivityMainBinding
import com.example.hive.model.adapters.SessionManager
import com.example.hive.viewmodel.EventListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hasLocationStarted = false
    private lateinit var viewModelEvent : EventListViewModel

    //time tracker
    private var startTimeMillis: Long = 0
    private var isTracking = false //Indicates if the user is currently tracking time
    private var elapsedTimeSeconds: Long = 0 // Total elapsed time in seconds

    private val polygonPoints = listOf(
        LatLng(4.6053640, -74.0666571),
        LatLng(4.6029762, -74.0677709),
        LatLng(4.6022922, -74.0661728),
        LatLng(4.6009240, -74.0667842),
        LatLng(4.5992985, -74.0660603),
        LatLng(4.5986837, -74.0635839),
        LatLng(4.5990685, -74.0611064),
        LatLng(4.6024247, -74.0607632),
        LatLng(4.6053533, -74.0666619),
        LatLng(4.6053640, -74.0666571)
    )

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Block the screen rotation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        FirebaseAnalytics.getInstance(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        var notificationSent = sessionManager.getNotification()

        if (!isUserLoggedIn()) {
            navigateToLogin()
        } else if (!notificationSent) {
            // Send the notification
            sendNotification()

            // Set the notification status to "sent" in shared preferences
            sessionManager.saveNotification(true)
        }

        replaceFragment(HomePageFragment())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Ask for background location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        if (!hasLocationStarted) {
            hasLocationStarted = true
            startLocationMonitoringService()
        }

        //TIME TRACKER
        //Get the elapsed time from the session manager
        elapsedTimeSeconds = sessionManager.getElapsedTime()

        //Start the timer if it is not already running
        if (!isTracking) {
            startTimeMillis = System.currentTimeMillis()
            isTracking = true
        }

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

    //TIME TRACKER STATUS

    //time tracker reset cases
    private fun restartTimer() {
        if (!isTracking) {
            startTimeMillis = System.currentTimeMillis()
            isTracking = true
        }
    }

    override fun onResume(){
        super.onResume()
        //Restart the timer if it is not already running
        restartTimer()
    }
    override fun onRestart() {
        super.onRestart()
        //Restart the timer if it is not already running
        restartTimer()
    }

    // time tracker stop cases
    private fun stopTimer() {
        if (isTracking) {
            val endTimeMillis = System.currentTimeMillis()
            elapsedTimeSeconds += (endTimeMillis-startTimeMillis)/1000
            isTracking = false
        }

        sessionManager.saveElapsedTime(elapsedTimeSeconds)
    }

    override fun onPause(){
        super.onPause()
        //Stop the timer if it is running
        stopTimer()
    }
    override fun onStop(){
        super.onStop()
        //Stop the timer if it is running
        stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Save the elapsed time in the session manager
        sessionManager.saveElapsedTime(elapsedTimeSeconds)
        sessionManager.saveDatabase(false)
    }

    fun replaceFragment(fragment: Fragment) {
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

    private fun checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            location?.let {
                val userLatLng = LatLng(location.latitude, location.longitude)
                val isInsidePolygon = isPointInPolygon(userLatLng, polygonPoints)
                if (isInsidePolygon) {
                }
            }
        }
    }
    private fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        val x = point.latitude
        val y = point.longitude
        var isInside = false

        var j = polygon.size - 1
        for (i in polygon.indices) {
            val xi = polygon[i].latitude
            val yi = polygon[i].longitude
            val xj = polygon[j].latitude
            val yj = polygon[j].longitude

            val intersect =
                ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)
            if (intersect) isInside = !isInside

            j = i
        }
        return isInside
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkUserLocation()
            }
        }
    }

    private fun startLocationMonitoringService() {
        val intent = Intent(this, LocationMonitoringService::class.java)
        startService(intent)
    }

    private fun sendNotification() {
        val channelId = "123"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_star)
            .setContentTitle(getString(R.string.main_welcome))
            .setContentText(getString(R.string.main_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Welcome Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }



}