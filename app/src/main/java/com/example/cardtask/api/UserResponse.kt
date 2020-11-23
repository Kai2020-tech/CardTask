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

data class AddUserResponse(
    @SerializedName("group_data")
    val groupData: GroupData = GroupData(),
    @SerializedName("status")
    val status: Boolean = false // true
) {
    data class GroupData(
        @SerializedName("card_id")
        val cardId: String = "", // 1
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-21T08:44:05.000000Z
        @SerializedName("id")
        val id: Int = 0, // 19
        @SerializedName("updated_at")
        val updatedAt: String = "", // 2020-09-21T08:44:05.000000Z
        @SerializedName("users_id")
        val usersId: Int = 0 // 2
    )
}