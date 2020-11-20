package com.example.cardtask.api
import com.google.gson.annotations.SerializedName

//查詢card的users
data class UserGroupResponse(
    @SerializedName("status")
    val status: Boolean = false, // true
    @SerializedName("users_data")
    val usersData: List<UsersData> = listOf()
) {
    data class UsersData(
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-02T08:07:20.000000Z
        @SerializedName("email")
        val email: String = "", // ishida624@gmail.com
        @SerializedName("id")
        val id: Int = 0, // 2
        @SerializedName("image")
        val image: String = "", // null
        @SerializedName("pivot")
        val pivot: Pivot = Pivot(),
        @SerializedName("updated_at")
        val updatedAt: String = "", // 2020-09-21T07:54:41.000000Z
        @SerializedName("username")
        val username: String = "" // admin
    ) {
        data class Pivot(
            @SerializedName("card_id")
            val cardId: Int = 0, // 1
            @SerializedName("users_id")
            val usersId: Int = 0 // 2
        )
    }
}