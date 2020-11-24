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