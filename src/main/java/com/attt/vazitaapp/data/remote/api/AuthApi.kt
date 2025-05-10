package com.attt.vazitaapp.data.remote.api


import com.attt.vazitaapp.data.model.LoginRequest
import com.attt.vazitaapp.data.model.LoginResponse
import com.attt.vazitaapp.data.model.TokenRefreshResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/refresh")
    fun refreshToken(@Body refreshTokenRequest: RequestBody): Call<TokenRefreshResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Void>
}