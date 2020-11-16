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
    //    @Headers("Content-type: application/json","Accept: application/json")
    @POST("register")   //註冊
    fun register(
        @Body user: SendRegister
    ): Call<RegisterResponse>

    @POST("userToken")  //登入
    fun login(
        @Body user: SendLogin
    ): Call<LoginResponse>

//    @GET("userToken")  //取得使用者
//    fun getUser(
//        @Header("userToken") userToken: String
//    ): Call<LoginResponse>

    @Headers("Content-type: application/json")  //取得所有資料
    @GET("card")
    fun getCard(@Header("userToken") userToken: String): Call<CardResponse>

    @POST("card")   //新增卡片
    fun newCard(
        @Header("userToken") userToken: String,
        @Body cardName: NewCard
    ): Call<NewCardResponse>

    @DELETE("card/{id}")   //刪除卡片
    fun deleteCard(
        @Header("userToken") userToken: String,
        @Path("id") id: Int
    ): Call<DeleteCardResponse>

    @PUT("card/{id}")   //更新卡片
    fun updateCard(
        @Header("userToken") userToken: String,
        @Path("id") id: Int,
        @Body cardName: NewCard
    ): Call<UpdateCardResponse>

//    @POST("task")   //新增Task
//    @FormUrlEncoded
//    fun newTask(
//        @Header("userToken") userToken: String,
//        @Field("card_id") card_id: Int = 0,
//        @Field("image") image: String?,
//        @Field("title") taskName: String?,
//        @Field("tag") color: String?,
//        @Field("description") description: String?
//    ): Call<NewTaskResponse>

    @POST("task")   //新增Task
    @Multipart
    fun createTask(
        @Header("userToken") userToken: String,
        @Part("card_id") card_id: Int,
        @Part image: MultipartBody.Part?,
        @Part taskName: MultipartBody.Part?,
        @Part color: MultipartBody.Part?,
        @Part description: MultipartBody.Part?
    ): Call<NewTaskResponse>

    @POST("task/{taskId}")   //更新Task
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

//    @PUT("task/{taskId}")   //更新Task
//    @Multipart
//    fun updateTask(
//        @Path("taskId") taskId: Int,
//        @Header("userToken") userToken: String,
//        @Body requestBody: RequestBody
//    ): Call<UpdateTaskResponse>
//    @PUT("task/{taskId}")   //更新Task
//    @Multipart
//    fun updateImage(
//        @Header("userToken") userToken: String,
//        @Part image: MultipartBody.Part?
//    ): Call<UpdateTaskResponse>

    @DELETE("task/{id}")   //刪除Task
    fun deleteTask(
        @Header("userToken") userToken: String,
        @Path("id") id: Int
    ): Call<DeleteTaskResponse>
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