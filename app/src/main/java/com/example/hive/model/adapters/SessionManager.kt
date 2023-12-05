package com.example.hive.model.adapters

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.hive.model.models.UserSession

class SessionManager(context: Context) {
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(
            KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        .build()

    // Create an EncryptedSharedPreferences instance
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val sharedPreferencesDatabase: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "database",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val sharedPreferencesNotification: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "notification",
        masterKey,
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
        // Create a new editor with the current master key alias
        editor.clear().apply()
    }
}