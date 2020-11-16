package com.example.cardtask.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class MyCallback<T> : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.d("network failure", t.message ?: "")
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(call, response)
        } else {
            Log.d("res", "${response.body().toString()}")
        }
    }
    open fun notSuccess() {}
    abstract fun onSuccess(call: Call<T>, response: Response<T>)
//    abstract fun onFailure()
//    abstract fun notSuccess(call: Call<T>, response: Response<T>)
}
//上面的class用於他處,即爲以下用法

//                .enqueue(object : retrofit2.Callback<NewTaskResponse> {
//                    override fun onFailure(call: Call<NewTaskResponse>, t: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onResponse(
//                        call: Call<NewTaskResponse>,
//                        response: Response<NewTaskResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(
//                                activity,
//                                "[$taskTitle] 已新增",
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            Log.d("Success!", "New task OK")
//                        }
//                    }
//                })