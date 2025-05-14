package com.example.yayaya.api

import android.content.Context
import android.content.SharedPreferences

class sessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Function to save tokens
    fun saveAuthToken(accessToken: String) {
        editor.putString("akses", accessToken)
        editor.apply()
    }

    fun saveRefreshToken(refreshToken: String) {
        editor.putString("refresh", refreshToken)
        editor.apply()
    }

    // Function to get tokens
    fun getAuthToken(): String? {
        return sharedPreferences.getString("akses", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh", null)
    }

    // Function to clear session
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
