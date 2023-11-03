package com.example.hive.view

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hive.R
import com.google.android.gms.maps.model.LatLng

class LocationMonitoringService : Service() {

    private lateinit var locationManager: LocationManager
    private var serviceStarted = false

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

    private val locationListener = LocationListener { location ->
        val userLatLng = LatLng(location.latitude, location.longitude)
        println("User location: $userLatLng")
        val isInsidePolygon = isPointInPolygon(userLatLng, polygonPoints)
        if (isInsidePolygon) {
            val notification = createNotification()
            startForeground(1, notification)
            sendNotification()
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

    private fun createNotification(): Notification {
        val channelId = "my_channel_id"

        // Create an explicit intent for the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Create a PendingIntent for launching the MainActivity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification using NotificationCompat.Builder
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.cas_tittle))
            .setContentText(getString(R.string.cas_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        // Check if the device is running Android Oreo (API 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    private fun sendNotification() {
        val channelId = "my_channel_id"

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.cas_tittle))
            .setContentText(getString(R.string.cas_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!serviceStarted) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return START_STICKY
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
            serviceStarted = true
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val CHANNEL_ID = "my_channel_id"
        private const val NOTIFICATION_ID = 1
        private const val MIN_TIME_BETWEEN_UPDATES = 10000L
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 100.0f
    }
}