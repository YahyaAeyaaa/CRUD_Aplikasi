package com.example.yayaya

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yayaya.api.ApiClient
import com.example.yayaya.request.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnRegister: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputName     = findViewById(R.id.inputName)
        inputEmail    = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        btnRegister   = findViewById(R.id.BtnRegister)

        btnRegister.setOnClickListener {
            val name  = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val pass  = inputPassword.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                doRegister(name, email, pass)
            }
        }

        setupLoginSpan()
    }

    private fun setupLoginSpan() {
        val tv = findViewById<TextView>(R.id.tvHaveAccount)
        val text = "Have an account? Login"
        val spannable = SpannableString(text)

        val start = text.indexOf("Login")
        val end   = start + "Login".length

        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, Login::class.java))
                finish()
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.BLUE
            }
        }

        spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spannable
        tv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun doRegister(name: String, email: String, pass: String) {
        val req = RegisterRequest(name, email, pass)
        CoroutineScope(Dispatchers.IO).launch {
            val res = ApiClient.authService.register(req)
            withContext(Dispatchers.Main) {
                if (res.isSuccessful && res.body() != null) {
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("nama", name)
                        .apply()

                    Toast.makeText(
                        this@MainActivity,
                        res.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@MainActivity, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Register gagal: ${res.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}