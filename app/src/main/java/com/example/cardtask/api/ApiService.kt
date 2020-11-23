package com.example.cardtask.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://dodo.gill.gq/api/"

var token = ""

interface ApiService {

    //註冊
    @POST("register")
    fun register(@Body user: SendRegister): Call<RegisterResponse>

    //登入
    @POST("userToken")
    fun login(@Body user: SendLogin): Call<LoginResponse>

    //取得所有資料
    @Headers("Content-type: application/json")
    @GET("card")
    fun getCard(@Header("userToken") userToken: String): Call<CardResponse>

    //取得卡片的使用者
    @GET("groups/card/users/{cardId}")
    fun getUsers(
        @Header("userToken") userToken: String,
        @Path("cardId") id: Int
    ): Call<UserGroupResponse>

    //新增卡片
    @POST("card")
    fun newCard(
        @Header("userToken") userToken: String,
        @Body cardName: NewCard
    ): Call<NewCardResponse>

    //刪除卡片
    @DELETE("card/{id}")
    fun deleteCard(
        @Header("userToken") userToken: String,
        @Path("id") id: Int
    ): Call<DeleteCardResponse>

    //更新卡片
    @PUT("card/{id}")
    fun updateCard(
        @Header("userToken") userToken: String,
        @Path("id") id: Int,
        @Body cardName: NewCard
    ): Call<UpdateCardResponse>

    //新增Task
    @POST("task")
    @Multipart
    fun createTask(
        @Header("userToken") userToken: String,
        @Part("card_id") card_id: Int,
        @Part image: MultipartBody.Part?,
        @Part taskName: MultipartBody.Part?,
        @Part color: MultipartBody.Part?,
        @Part description: MultipartBody.Part?
    ): Call<NewTaskResponse>

    //更新Task
    @POST("task/{taskId}")
    @Multipart
    fun updateTask(
        @Path("taskId") taskId: Int,
        @Header("userToken") userToken: String,
        @Part("card_id") card_id: Int,
        @Part method: MultipartBody.Part?,
        @Part taskName: MultipartBody.Part?,
        @Part color: MultipartBody.Part?,
        @Part description: MultipartBody.Part?,
        @Part image: MultipartBody.Part?,
        @Part("delete_image") del_img: Boolean?
    ): Call<UpdateTaskResponse>

    //刪除Task
    @DELETE("task/{id}")
    fun deleteTask(
        @Header("userToken") userToken: String,
        @Path("id") id: Int
    ): Call<DeleteTaskResponse>

    //卡片加入使用者
    @POST("groups/{cardId}")
    fun addUser(
        @Header("userToken") userToken: String,
        @Path("cardId") card_id: Int,
        @Body email: AddUser
    ): Call<AddUserResponse>
}

object Api {
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: ApiService = retrofit.create(
        ApiService::class.java
    )
}