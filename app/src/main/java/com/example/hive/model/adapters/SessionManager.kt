package com.example.hive.model.adapters

import android.content.Context
import android.content.SharedPreferences
import com.example.hive.model.models.UserSession

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserSession(session: UserSession) {
        editor.putString("auth_token", session.authToken)
        editor.putString("user_id", session.userId)
        editor.apply()
    }

    fun getUserSession(): UserSession {
        val authToken = sharedPreferences.getString("auth_token", null)
        val userId = sharedPreferences.getString("user_id", null)
        return UserSession(authToken, userId)
    }

    fun clearSession() {
        editor.clear().apply()
    }
}