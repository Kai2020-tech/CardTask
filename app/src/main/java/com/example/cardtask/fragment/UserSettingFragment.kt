package com.example.cardtask.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.cardtask.R
import com.example.cardtask.api.Api
import com.example.cardtask.api.ChangeInfo
import com.example.cardtask.api.ChangeInfoResponse
import com.example.cardtask.api.token
import com.example.cardtask.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_user_setting.*
import kotlinx.android.synthetic.main.fragment_user_setting.view.*
import retrofit2.Call
import retrofit2.Response


class UserSettingFragment : Fragment() {

    lateinit var rootView: View
    private var userPhotoPath = ""
    private var userEmail = ""
    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userPhotoPath = it.getString("userPhotoPath").toString()
            userEmail = it.getString("userEmail").toString()
            userName = it.getString("userName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_user_setting, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  背景圖用
        gradientChart_userSettingFragment.chartValues = arrayOf(
            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f
        )

        Glide.with(this).load(userPhotoPath).transform(CircleCrop()).into(rootView.img_userSettingPhoto)
        rootView.tv_userSettingName.text = userName
        rootView.tv_userSettingEmail.text = userEmail

//        rootView.ed_userSettingPassword.setText("")
//        rootView.ed_passwordDoubleCheck.setText("")

        //修改資料
        rootView.btn_settingChange.setOnClickListener {
            rootView.btn_settingChange.visibility = View.INVISIBLE
            rootView.textView3.visibility = View.VISIBLE
            rootView.textView4.visibility = View.VISIBLE
            rootView.textView5.visibility = View.VISIBLE

            rootView.ed_userSettingName.visibility = View.VISIBLE
//            rootView.ed_userSettingPassword.visibility = View.VISIBLE
            rootView.ed_passwordLayout.visibility = View.VISIBLE
            rootView.ed_passwordDoubleCheckLayout.visibility = View.VISIBLE

            rootView.btn_settingConfirm.visibility = View.VISIBLE
            rootView.btn_settingCancel.visibility = View.VISIBLE
        }
        //取消修改
        rootView.btn_settingCancel.setOnClickListener {
            rootView.btn_settingChange.visibility = View.VISIBLE
            rootView.textView3.visibility = View.INVISIBLE
            rootView.textView4.visibility = View.INVISIBLE
            rootView.textView5.visibility = View.INVISIBLE

            rootView.ed_userSettingName.visibility = View.INVISIBLE
            rootView.ed_userSettingName.text.clear()
            rootView.ed_passwordLayout.visibility = View.INVISIBLE
            rootView.ed_userSettingPassword.text?.clear()
            rootView.ed_passwordDoubleCheckLayout.visibility = View.INVISIBLE
            rootView.ed_passwordDoubleCheck.text?.clear()

            rootView.btn_settingConfirm.visibility = View.INVISIBLE
            rootView.btn_settingCancel.visibility = View.INVISIBLE
        }
        //確認修改
        rootView.btn_settingConfirm.setOnClickListener {
            if (rootView.ed_userSettingName.text.isEmpty()) {
                if (rootView.ed_userSettingPassword.text!!.isNotEmpty()) {
                    if (rootView.ed_userSettingPassword.text.toString() == rootView.ed_passwordDoubleCheck.text.toString()) {
                        changUserPassword()
                    } else showToast("請核對密碼")
                }
            }
            if (rootView.ed_userSettingName.text.isNotEmpty()) {
                if (rootView.ed_userSettingPassword.text!!.isEmpty()) {
                    changUserName()
                } else changUserNameAndPassword()
            }
        }
    }

    private fun changUserName() {
        Api.retrofitService.changUserName(token, rootView.ed_userSettingName.text.toString())
            .enqueue(object : retrofit2.Callback<ChangeInfoResponse> {
                override fun onFailure(call: Call<ChangeInfoResponse>, t: Throwable) {
                    Log.e("error", "change user info error")
                }

                override fun onResponse(call: Call<ChangeInfoResponse>, response: Response<ChangeInfoResponse>) {
                    if (response.isSuccessful) {
                        showToast("暱稱 已修改")
                    }else showToast("${response.errorBody()}")
                }
            })
    }

    private fun changUserPassword() {
        Api.retrofitService.changUserPassword(token, rootView.ed_userSettingPassword.text.toString())
            .enqueue(object : retrofit2.Callback<ChangeInfoResponse> {
                override fun onFailure(call: Call<ChangeInfoResponse>, t: Throwable) {
                    Log.e("error", "change user info error")
                }

                override fun onResponse(call: Call<ChangeInfoResponse>, response: Response<ChangeInfoResponse>) {
                    if (response.isSuccessful) {
                        showToast("密碼 已修改")
                    }else showToast("${response.errorBody()}")
                }
            })
    }

    private fun changUserNameAndPassword() {
        Api.retrofitService.changUserNameAndPassword(
            token,
            rootView.ed_userSettingName.text.toString(),
            rootView.ed_userSettingPassword.text.toString()
        )
            .enqueue(object : retrofit2.Callback<ChangeInfoResponse> {
                override fun onFailure(call: Call<ChangeInfoResponse>, t: Throwable) {
                    Log.e("error", "change user info error")
                }

                override fun onResponse(call: Call<ChangeInfoResponse>, response: Response<ChangeInfoResponse>) {
                    if (response.isSuccessful) {
                        showToast("暱稱,密碼 已修改")
                    }else { //error body要以下方式去接
                        val gson = Gson()
                        val type = object : TypeToken<ChangeInfoResponse>() {}.type
                        var errorResponse: ChangeInfoResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                        showToast(errorResponse.error)
                    }
                }
            })
    }


}