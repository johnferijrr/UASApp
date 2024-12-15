package com.example.retrofit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofit.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}