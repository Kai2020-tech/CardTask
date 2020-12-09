package com.example.cardtask.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.cardtask.R
import com.example.cardtask.api.Api
import com.example.cardtask.api.ChangeInfoResponse
import com.example.cardtask.api.UploadUserPhoto
import com.example.cardtask.api.token
import com.example.cardtask.createMultipartBody
import com.example.cardtask.getPhoto
import com.example.cardtask.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_user_setting.*
import kotlinx.android.synthetic.main.fragment_user_setting.view.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UserSettingFragment : Fragment() {

    lateinit var rootView: View
    private var userPhotoPath = ""
    private var userEmail = ""
    private var userName = ""
    private var photoFile: File? = null
    private var galleryUri: Uri? = null

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

        //上傳圖片
        rootView.btn_takePhoto.setOnClickListener {
            photoFile = getPhoto(activity)
        }

        //修改資料
        rootView.btn_settingChange.setOnClickListener {
            rootView.btn_settingChange.visibility = View.INVISIBLE
            rootView.textView3.visibility = View.VISIBLE
            rootView.textView4.visibility = View.VISIBLE
            rootView.textView5.visibility = View.VISIBLE

            rootView.ed_userSettingName.visibility = View.VISIBLE
            rootView.ed_passwordLayout.visibility = View.VISIBLE
            rootView.ed_passwordDoubleCheckLayout.visibility = View.VISIBLE

            rootView.btn_settingConfirm.visibility = View.VISIBLE
            rootView.btn_settingCancel.visibility = View.VISIBLE

            rootView.btn_takePhoto.visibility = View.INVISIBLE
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

            rootView.btn_takePhoto.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//相機, intent回來
        if (requestCode == MainFragment.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(photoFile).transform(CircleCrop()).into(rootView.img_userSettingPhoto)
            Log.d("uri camera", photoFile?.path.toString())
            rootView.img_userSettingPhoto.background = null

            changeUserPhoto()

        }
//相簿, intent回來
        else if (requestCode == MainFragment.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            galleryUri = data?.data!!
            Log.d("gallery", "uri: $galleryUri")
            Glide.with(this).load(galleryUri).transform(CircleCrop()).into(rootView.img_userSettingPhoto)
            rootView.img_userSettingPhoto.background = null

            changeUserPhoto()
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
                    } else errorMessage(response)
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
                    } else errorMessage(response)
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
                    } else { //error body要以下方式去接
                        errorMessage(response)
                    }
                }
            })
    }

    private fun errorMessage(response: Response<ChangeInfoResponse>) {
        val gson = Gson()
        val type = object : TypeToken<ChangeInfoResponse>() {}.type
        var errorResponse: ChangeInfoResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
        showToast(errorResponse.error)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun changeUserPhoto() {
        rootView.btn_settingChange.visibility = View.INVISIBLE
        rootView.btn_uploadPhoto.visibility = View.VISIBLE
        rootView.btn_cancelUpload.visibility = View.VISIBLE

        rootView.btn_cancelUpload.setOnClickListener {
            Glide.with(this).load(userPhotoPath).transform(CircleCrop()).into(rootView.img_userSettingPhoto)
            rootView.btn_settingChange.visibility = View.VISIBLE
            rootView.btn_uploadPhoto.visibility = View.INVISIBLE
            rootView.btn_cancelUpload.visibility = View.INVISIBLE
        }

        rootView.btn_uploadPhoto.setOnClickListener {
            uploadUserPhoto()
            rootView.btn_settingChange.visibility = View.VISIBLE
            rootView.btn_uploadPhoto.visibility = View.INVISIBLE
            rootView.btn_cancelUpload.visibility = View.INVISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun uploadUserPhoto() {
        val file = File(photoFile?.path ?: "")
        val imageBody: MultipartBody.Part? =
            if (galleryUri != null) createMultipartBody(galleryUri) else createMultipartBody(file)
        Api.retrofitService.uploadUserPhoto(token, imageBody)
            .enqueue(object : Callback<UploadUserPhoto> {
                override fun onFailure(call: Call<UploadUserPhoto>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call<UploadUserPhoto>, response: Response<UploadUserPhoto>) {
                    if (response.isSuccessful) {
                        showToast("圖片已更新")
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<UploadUserPhoto>() {}.type
                        var errorResponse: UploadUserPhoto = gson.fromJson(response.errorBody()!!.charStream(), type)
                        showToast(errorResponse.error)
                    }
                }
            })
    }



    //    override fun onBackPressed() {
//        super.onBackPressed()
//        if(supportFragmentManager.backStackEntryCount ==0){
//            supportFragmentManager.beginTransaction().apply {
//                replace(R.id.frame_layout, cardFragment, "cardFragment")
//                commit()
//            }
//        }
//        val pref = SharedPreferences(this)
//        val cardFragment = supportFragmentManager.findFragmentByTag("cardFragment")
//        if (cardFragment != null) {
//            if (!pref.getData().isNullOrEmpty() && supportFragmentManager.backStackEntryCount == 1) {
//                finishAffinity()    //當token登入中且fragment只有一個,按back時,結束整個app
//            }
//        }
//    }


}