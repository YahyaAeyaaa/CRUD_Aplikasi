package com.example.yayaya

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yayaya.api.ApiClient
import com.example.yayaya.request.RefreshTokenRequest
import com.example.yayaya.response.User
import com.example.yayaya.ui.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class Home : AppCompatActivity() {
    private lateinit var logoutButton: Button
    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: UserAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val checkTokenInterval: Long = TimeUnit.SECONDS.toMillis(5)

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val jsonObject = JSONObject(payload)
                val expTime = jsonObject.getLong("exp") * 1000
                System.currentTimeMillis() > expTime
            } else true
        } catch (e: Exception) {
            true
        }
    }

    private fun startTokenChecker() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkAndRefreshToken()
                handler.postDelayed(this, checkTokenInterval)
            }
        }, checkTokenInterval)
    }

    private fun checkAndRefreshToken() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("akses", "")
        val refreshToken = sharedPreferences.getString("refresh", "")

        if (!accessToken.isNullOrEmpty() && isTokenExpired(accessToken)) {
            if (!refreshToken.isNullOrEmpty() && !isTokenExpired(refreshToken)) {
                refreshAccessToken(refreshToken)
            } else {
                logoutAndRedirectToLogin()
            }
        }
    }

    private fun refreshAccessToken(refreshToken: String) {
        val request = RefreshTokenRequest(refreshToken)

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.authService.refreshToken(request)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val newAccessToken = it.accessToken
                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("akses", newAccessToken)
                            apply()
                        }
                    }
                } else {
                    logoutAndRedirectToLogin()
                    Toast.makeText(this@Home, "Refresh token gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logoutAndRedirectToLogin() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("akses", null)

        CoroutineScope(Dispatchers.IO).launch {
            val message = if (!accessToken.isNullOrEmpty()) {
                try {
                    val response = ApiClient.authService.logout("Bearer $accessToken")
                    if (response.isSuccessful) "Logout Berhasil" else "Logout gagal di server"
                } catch (e: Exception) {
                    "Terjadi kesalahan jaringan saat logout"
                }
            } else {
                "Token tidak ditemukan, langsung logout"
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@Home, message, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().clear().apply()

                handler.removeCallbacksAndMessages(null)
                startActivity(Intent(this@Home, Login::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logoutButton)
        rvUsers = findViewById(R.id.rvUsers)

        logoutButton.setOnClickListener {
            logoutAndRedirectToLogin()
        }

        startTokenChecker()

        adapter = UserAdapter(mutableListOf(),
            onEdit = { user -> /* TODO */ },
            onDelete = { user -> deleteUser(user.id) }
        )
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("akses", null) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.authService.getAllUsers("Bearer $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { adapter.setData(it) }
                } else {
                    Toast.makeText(this@Home, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteUser(id: Int) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("akses", null) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.authService.deleteUser("Bearer $token", id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Home, "User dihapus", Toast.LENGTH_SHORT).show()
                    fetchAllUsers()
                } else {
                    Toast.makeText(this@Home, "Hapus gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
