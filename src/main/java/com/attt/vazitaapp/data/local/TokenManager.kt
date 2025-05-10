package com.attt.vazitaapp.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.attt.vazitaapp.VazitaApp

class TokenManager(private val context: Context = VazitaApp.instance) {
    companion object {
        private const val PREF_NAME = "secure_token_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_CENTER_ID = "center_id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        encryptedPrefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return encryptedPrefs.getString(KEY_TOKEN, null)
    }

    fun saveRefreshToken(refreshToken: String) {
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun saveUserRole(role: Int) {
        encryptedPrefs.edit().putInt(KEY_USER_ROLE, role).apply()
    }

    fun getUserRole(): Int {
        return encryptedPrefs.getInt(KEY_USER_ROLE, -1)
    }

    fun saveCenterId(centerId: String) {
        encryptedPrefs.edit().putString(KEY_CENTER_ID, centerId).apply()
    }

    fun getCenterId(): String? {
        return encryptedPrefs.getString(KEY_CENTER_ID, null)
    }

    fun saveUserId(userId: String) {
        encryptedPrefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return encryptedPrefs.getString(KEY_USER_ID, null)
    }

    fun saveUserName(userName: String) {
        encryptedPrefs.edit().putString(KEY_USER_NAME, userName).apply()
    }

    fun getUserName(): String? {
        return encryptedPrefs.getString(KEY_USER_NAME, null)
    }

    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}