package com.example.cardtask

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.cardtask.api.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = SharedPreferences(this)
        if (!pref.getData().isNullOrEmpty()) {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java)) //啓動SecondActivity
            finish()    //將自己結束
        }


        btn_register.setOnClickListener {
            btn_cardConfirm.visibility = View.VISIBLE
            ed_name.visibility = View.VISIBLE
            btn_login.visibility = View.GONE
            btn_register.visibility = View.GONE
        }

        btn_cardConfirm.setOnClickListener {
            hideKeyboard(btn_cardConfirm, btn_cardConfirm)

            val user = SendRegister(
                ed_name.text.toString(),
                ed_email.text.toString(),
                ed_password.text.toString()
            )
            Api.retrofitService.register(user)
                .enqueue(object : retrofit2.Callback<RegisterResponse> {
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Log.e("Failed", t.toString())
                    }

                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {    //request 200,註冊成功
                            changeEdTextVisibility()
                            Toast.makeText(
                                this@MainActivity,
                                response.body()?.isSuccess.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Success!", response.body().toString())
                        } else {                          //request 400,註冊失敗
//                            val res = response.errorBody()?.string()
                            val gson = Gson()
                            val type = object : TypeToken<RegisterResponse>() {}.type
                            var errorResponse: RegisterResponse? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)
                            Log.d("Success!", response.errorBody()?.string())
                            Toast.makeText(
                                this@MainActivity,
                                errorResponse?.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }

        btn_login.setOnClickListener {
            hideKeyboard(btn_cardConfirm, btn_cardConfirm)

            val user = SendLogin(
                ed_email.text.toString(),
                ed_password.text.toString()
            )
            Api.retrofitService.login(user)
                .enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("Failed", t.toString())
                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {    //request 200,成功
                            changeEdTextVisibility()
                            token = response.body()?.login?.userToken.toString()
                            Toast.makeText(
                                this@MainActivity,
                                token,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Success!", "${response.body()?.isSuccess.toString()}  $token")
                            pref.saveData(token)
                            startActivity(
                                Intent(this@MainActivity, SecondActivity::class.java)
                            ) //啓動SecondActivity
                            finish()    //將自己結束
                        } else {                          //request 400,失敗
                            val gson = Gson()
                            val type = object : TypeToken<LoginResponse>() {}.type
                            var errorResponse: LoginResponse? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)
                            Log.d("fail!", response.errorBody()?.string())
                            Toast.makeText(this@MainActivity, errorResponse?.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

    }

    //    按返回鍵判斷是否在register
    override fun onBackPressed() {
        if (btn_cardConfirm.visibility == View.VISIBLE) {
            changeEdTextVisibility()

            Toast.makeText(this, "再按一次退出App", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()   //不在register的話,就會退出app
        }
    }

    //    隱藏鍵盤
    private fun hideKeyboard(view: View, nextFoocusView: View = view.rootView) {
        val imm = view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
        nextFoocusView.requestFocus()
    }

    fun changeEdTextVisibility() {
        btn_cardConfirm.visibility = View.GONE
        ed_name.visibility = View.INVISIBLE
        btn_login.visibility = View.VISIBLE
        btn_register.visibility = View.VISIBLE
    }
}