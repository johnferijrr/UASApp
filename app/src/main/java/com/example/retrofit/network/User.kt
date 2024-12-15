package com.example.retrofit.network

data class User(
    val _id: String,
    val username: String? = null,
    val password: String? = null,
    val data1: String? = null,
    val data2: String? = null, // Now data2 is a string
    val data3: String? = null  // data3 is already a string
)



