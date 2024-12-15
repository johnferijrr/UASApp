package com.example.retrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofit.databinding.ActivityUpdateUserBinding
import com.example.retrofit.network.ApiClient
import com.example.retrofit.network.User
import com.example.retrofit.network.UserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateUserBinding
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")
        if (userId == null) {
            Toast.makeText(this, "User ID is missing. Unable to update user.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set initial data to form
        binding.etUsername.setText(intent.getStringExtra("USERNAME") ?: "")
        binding.etPassword.setText(intent.getStringExtra("PASSWORD") ?: "")
        binding.etData1.setText(intent.getStringExtra("DATA1") ?: "")
        binding.etData2.setText(intent.getStringExtra("DATA2") ?: "")
        binding.etData3.setText(intent.getStringExtra("DATA3") ?: "")

        binding.btnSubmitUpdate.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val data1 = binding.etData1.text.toString().trim()
            val data2 = binding.etData2.text.toString().trim()
            val data3 = binding.etData3.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || data1.isEmpty() || data2.isEmpty() || data3.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUser = UserRequest(username, password, data1, data2, data3)
            Log.d("UpdateUserActivity", "Updating user with payload: $updatedUser")
            updateUser(userId!!, updatedUser)
        }
    }

    private fun updateUser(id: String, updatedUser: UserRequest) {
        ApiClient.service.updateUser(id, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("UpdateUserActivity", "Response Code: ${response.code()}")
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateUserActivity, "User updated successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UpdateUserActivity", "Failed to update user. Code: ${response.code()}, Error: $errorBody")
                    Toast.makeText(
                        this@UpdateUserActivity,
                        "Failed to update user: ${errorBody ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("UpdateUserActivity", "Error updating user", t)
                Toast.makeText(this@UpdateUserActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
