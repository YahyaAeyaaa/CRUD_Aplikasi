package com.example.yayaya.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String?,
    val accessToken: String?,
    val refreshToken: String?
)

data class RegisterResponse(
    val message: String?,
    val accessToken: String?,
    val refreshToken: String?
)

data class RefreshTokenResponse(
    val message: String?,
    val accessToken: String?
)

data class User(
    val id: Int,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val role: String
)

// 2) Wrapper untuk response GET /users
data class AllUsersResponse(
    val status: Int,
    val message: String,
    val data: List<User>
)
