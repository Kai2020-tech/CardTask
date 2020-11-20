package com.example.cardtask.api

import com.google.gson.annotations.SerializedName

//註冊回傳
data class RegisterResponse(
    @SerializedName("success")
    var isSuccess: Boolean = true,
    @SerializedName("error")
    var error: String? = ""
)
//登入回傳,token
data class LoginResponse(
    @SerializedName("success")
    var isSuccess: Boolean = true,
    @SerializedName("login_data")
    var login : LoginData = LoginData(),
    @SerializedName("error")
    var error: String? = ""
) {
    data class LoginData(
        @SerializedName("userToken")
        var userToken: String? = ""
    )
}
//註冊
data class SendRegister(
    val username: String,
    val email: String,
    val password: String
)
//登入
data class SendLogin(
    val email: String,
    val password: String
)

data class NewCard(
    @SerializedName("card_name")
    var cardName: String = "" // card name
)

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
        val image: Any = Any(), // null
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

//data class getUser(
//    @SerializedName("status")
//    val status: Boolean = false, // true
//    @SerializedName("user_data")
//    val userData: UserData = UserData()
//) {
//    data class UserData(
//        @SerializedName("created_at")
//        val createdAt: String = "", // 2020-09-15T04:01:19.000000Z
//        @SerializedName("email")
//        val email: String = "", // gill@gmail.com
//        @SerializedName("id")
//        val id: Int = 0, // 3
//        @SerializedName("image")
//        val image: String = "",
//        @SerializedName("updated_at")
//        val updatedAt: String = "", // 2020-09-18T09:20:10.000000Z
//        @SerializedName("username")
//        val username: String = "" // gill
//    )
//}