package com.example.hive.model.adapters

import android.content.Context
import android.content.SharedPreferences
import com.example.hive.model.models.UserSession

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val sharedPreferencesNotification: SharedPreferences = context.getSharedPreferences("notification", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserSession(session: UserSession) {
        editor.putString("auth_token", session.authToken)
        editor.putString("user_id", session.userId)
        editor.apply()
    }

    fun saveNotification(notification: Boolean) {
        val editorNotification: SharedPreferences.Editor = sharedPreferencesNotification.edit()
        editorNotification.putBoolean("notification", notification)
        editorNotification.apply()
    }

    fun getNotification(): Boolean {
        return sharedPreferencesNotification.getBoolean("notification", false)
    }

    fun getUserSession(): UserSession {
        val authToken = sharedPreferences.getString("auth_token", null)
        val userId = sharedPreferences.getString("user_id", null)
        return UserSession(authToken, userId)
    }

    //Time tracker functions
    fun saveElapsedTime(elapsedTimeSeconds: Long) {
        editor.putLong("time_tracker", elapsedTimeSeconds)
        editor.apply()
    }

    fun getElapsedTime(): Long {
        return sharedPreferences.getLong("time_tracker", 0)
    }

    fun clearSession() {
        editor.clear().apply()
    }
}