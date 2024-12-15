package com.example.retrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit.databinding.ActivityMainBinding
import com.example.retrofit.network.ApiClient
import com.example.retrofit.network.User
import com.example.retrofit.network.UserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val users = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchUsers() // Ambil semua data

        setupListeners()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(users) { user, actionType ->
            when (actionType) {
                UserAdapter.ActionType.DELETE -> deleteUser(user)
                UserAdapter.ActionType.UPDATE -> openUpdateUserActivity(user)
                UserAdapter.ActionType.DETAIL -> fetchUserDetail(user)
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }
    }

    private fun setupListeners() {
        binding.btnAddItem.setOnClickListener {
            val newUserRequest = generateUniqueUser()
            addUser(newUserRequest)
        }

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnFetchWithLimit.setOnClickListener {
            fetchUsers(limit = 5) // Ambil hanya 5 data
        }

        binding.btnFetchAll.setOnClickListener {
            fetchUsers() // Ambil semua data
        }
    }


    override fun onResume() {
        super.onResume()
        fetchUsers() // Auto-refresh data ketika kembali ke MainActivity
    }

    private fun fetchUsers(limit: Int? = null) {
        val call = if (limit != null) ApiClient.service.getUsersWithOptions("""{"limit": $limit}""")
        else ApiClient.service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        updateUserList(it)
                    } ?: showToast("No users found.")
                } else {
                    logAndToastError("Failed to fetch users", response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                logAndToastError("Error fetching users", t.message)
            }
        })
    }

    private fun updateUserList(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        userAdapter.notifyDataSetChanged() // Update RecyclerView
        Log.d("MainActivity", "Updated RecyclerView with ${users.size} users.")
    }

    private fun fetchUserDetail(user: User) {
        ApiClient.service.getUserDetail(user._id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { showUserDetailDialog(it) }
                } else {
                    showToast("Failed to fetch user detail.")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun deleteUser(user: User) {
        showConfirmationDialog(
            title = "Confirm Deletion",
            message = "Are you sure you want to delete user ${user.username}?",
            onConfirm = {
                ApiClient.service.deleteUser(user._id).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        if (response.isSuccessful) {
                            showToast("User deleted successfully.")
                            fetchUsers()
                        } else {
                            showToast("Failed to delete user.")
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        showToast("Error: ${t.message}")
                    }
                })
            }
        )
    }

    private fun openUpdateUserActivity(user: User) {
        val intent = Intent(this, UpdateUserActivity::class.java).apply {
            putExtra("USER_ID", user._id)
            putExtra("USERNAME", user.username ?: "")
            putExtra("PASSWORD", user.password ?: "")
            putExtra("DATA1", user.data1 ?: "")
            putExtra("DATA2", user.data2 ?: "")
            putExtra("DATA3", user.data3 ?: "")
        }
        Log.d("MainActivity", "Opening UpdateUserActivity with user: $user")
        startActivity(intent)
    }

    private fun generateUniqueUser(): UserRequest {
        val randomSuffix = System.currentTimeMillis().toString().takeLast(5)
        return UserRequest(
            username = "User$randomSuffix",
            password = "Pass$randomSuffix",
            data1 = "Data1_$randomSuffix",
            data2 = "Data2_$randomSuffix",
            data3 = "Data3_$randomSuffix"
        )
    }

    private fun addUser(userRequest: UserRequest) {
        ApiClient.service.addUser(userRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    showToast("User added successfully.")
                    fetchUsers()
                } else {
                    showToast("Failed to add user.")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showUserDetailDialog(user: User) {
        AlertDialog.Builder(this).apply {
            setTitle("User Details")
            setMessage(
                """
                Username: ${user.username ?: "N/A"}
                Password: ${user.password ?: "N/A"}
                Data1: ${user.data1 ?: "N/A"}
                Data2: ${user.data2 ?: "N/A"}
                Data3: ${user.data3 ?: "N/A"}
                """.trimIndent()
            )
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Yes") { _, _ -> onConfirm() }
            setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logAndToastError(message: String, error: String?) {
        Log.e("MainActivity", "$message: $error")
        showToast(message)
    }
}
