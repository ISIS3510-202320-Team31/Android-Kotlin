package com.example.hive.model.adapters

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.hive.model.models.UserSession

class SessionManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "user_session",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val sharedPreferencesDatabase = EncryptedSharedPreferences.create(
        "database",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val sharedPreferencesNotification: SharedPreferences = EncryptedSharedPreferences.create(
        "notification",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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

    fun saveDatabase(database: Boolean) {
        val editorDatabase: SharedPreferences.Editor = sharedPreferencesDatabase.edit()
        editorDatabase.putBoolean("database", database)
        editorDatabase.apply()
    }

    fun getDatabase(): Boolean {
        return sharedPreferencesDatabase.getBoolean("database", false)
    }

    fun getNotification(): Boolean {
        return sharedPreferencesNotification.getBoolean("notification", false)
    }

    fun getUserSession(): UserSession {
        val authToken = sharedPreferences.getString("auth_token", null)
        val userId = sharedPreferences.getString("user_id", null)
        return UserSession(authToken, userId)
    }

    // Time tracker functions
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