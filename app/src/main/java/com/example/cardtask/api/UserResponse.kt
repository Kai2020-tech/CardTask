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

// 群組卡片加入使用者
data class AddUser(
    @SerializedName("email")
    val email: String = "" // card name
)

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

//群組卡片刪除使用者
data class DelUser(
    @SerializedName("user_id")
    val usersId: Int = 0 // 2
)

data class DelUserResponse(
    @SerializedName("status")
    val status: Boolean = false // true
)

//修改暱稱,密碼
data class ChangeInfoResponse(
    @SerializedName("error")
    val error: String = "", // password should over 8 characters and only 0-9,a-z,A-Z.
    @SerializedName("status")
    val status: Boolean = false // false
)

//上傳頭像
data class UploadUserPhoto(
    @SerializedName("error")
    val error: String = "", // upload error
    @SerializedName("status")
    val status: Boolean? = false // false
)