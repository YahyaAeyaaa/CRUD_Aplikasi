package com.example.yayaya.api

import com.example.yayaya.request.LoginRequest
import com.example.yayaya.request.RegisterRequest
import com.example.yayaya.request.RefreshTokenRequest
import com.example.yayaya.response.LoginResponse
import com.example.yayaya.response.RegisterResponse
import com.example.yayaya.response.RefreshTokenResponse
import com.example.yayaya.response.AllUsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path

interface AuthService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register") // ✅ Diperbaiki dari "auth/register" menjadi "register"
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Void>

    @GET("users")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<AllUsersResponse>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Void>
}
