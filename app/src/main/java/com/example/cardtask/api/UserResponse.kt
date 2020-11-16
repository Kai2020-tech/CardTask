package com.example.cardtask.api
import com.google.gson.annotations.SerializedName


data class UserResponse(
    @SerializedName("status")
    val status: Boolean = false, // true
    @SerializedName("user_data")
    val userData: UserData = UserData()
) {
    data class UserData(
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-15T04:01:19.000000Z
        @SerializedName("email")
        var email: String = "", // gill@gmail.com
        @SerializedName("id")
        val id: Int = 0, // 3
        @SerializedName("image")
        var image: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = "", // 2020-09-18T09:20:10.000000Z
        @SerializedName("username")
        var username: String = "" // gill
    )
}