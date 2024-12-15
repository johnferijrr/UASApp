package com.example.retrofit.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
//    @GET("sxNVU/user")
    fun getUsers(): Call<List<User>>

    @GET("sxNVU/user")
    fun getUsersWithOptions(
        @Query("options") options: String
    ): Call<List<User>>

    @GET("sxNVU/user/{id}")
    fun getUserDetail(@Path("id") id: String): Call<User>

    @POST("sxNVU/user")
    fun addUser(@Body userRequest: UserRequest): Call<User>

    @POST("sxNVU/user/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Body updatedUser: UserRequest
    ): Call<User>

    @DELETE("sxNVU/user/{id}")
    fun deleteUser(@Path("id") id: String): Call<Unit>
}
