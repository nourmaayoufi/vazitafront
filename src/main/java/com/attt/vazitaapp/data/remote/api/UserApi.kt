package com.attt.vazitaapp.data.remote.api


import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.model.UserCreateRequest
import com.attt.vazitaapp.data.model.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<User>>

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<User>

    @POST("users")
    suspend fun createUser(@Body userCreateRequest: UserCreateRequest): Response<User>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<User>

    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): Response<Void>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>
}