package com.example.cardtask

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cardtask.api.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_forget_pw.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences

    //google login
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//  背景圖用
        gradientChart.chartValues = arrayOf(
            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f
        )
//      檢查之前有無已登入存token
        pref = SharedPreferences(this)
        if (!pref.getData().isNullOrEmpty()) {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java)) //啓動SecondActivity
            finish()    //將自己結束
        }
//google login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("609834348003-pbpsen9j6bd3j2eso8g5pbc64c5bnfrn.apps.googleusercontent.com") //OAUTH用戶端ID(Web client (Auto-created for Google Sign-in))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        if (pref.getData().isNullOrEmpty()) signOut()

        btn_google_login.setOnClickListener {
            googleSignIn()
        }

        btn_register.setOnClickListener {
            btn_cardConfirm.visibility = View.VISIBLE
            ed_name.visibility = View.VISIBLE
            btn_login.visibility = View.GONE
            btn_register.visibility = View.GONE
            btn_forgetPassword.visibility = View.GONE
            btn_google_login.visibility = View.GONE
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

        btn_forgetPassword.setOnClickListener {
            val emailDialog = LayoutInflater.from(this).inflate(R.layout.dialog_forget_pw, null)
            val dialogBuild = AlertDialog.Builder(this)
            val dialog = dialogBuild
                .setView(emailDialog)
                .setOnDismissListener {
                    (emailDialog.parent as ViewGroup).removeView(emailDialog)
                }
                .show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) //set dialog background transparent

            emailDialog.btn_forgetPw_cancel.setOnClickListener {
                dialog.dismiss()
            }

            emailDialog.btn_forgetPw_confirm.setOnClickListener {
                val email = emailDialog.ed_forgetPwEmail.text.toString()
                if(email.isNotEmpty()){
                    sendEmail(email)
                }else{
                    Toast.makeText(this, "請輸入email", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID", googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            val googleToken = GoogleToken()
            googleToken.idToken = googleIdToken
            sendGoogleToken(googleToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    //Intent google login
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }
    //google login

    //將google token傳給後端,再將後端token存下並啓動登入
    private fun sendGoogleToken(googleToken: GoogleToken) {
        Api.retrofitService.googleLogin(googleToken).enqueue(object : Callback<GoogleLoginResponse> {
            override fun onFailure(call: Call<GoogleLoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<GoogleLoginResponse>, response: Response<GoogleLoginResponse>) {
                if (response.isSuccessful) {
                    token = response.body()!!.loginData.userToken
                    pref.saveData(token)
                    startActivity(
                        Intent(this@MainActivity, SecondActivity::class.java)
                    ) //啓動SecondActivity
                    finish()    //將自己MainActivity結束
                }
            }
        })
    }

    //忘記密碼,發送email給後端
    private fun sendEmail(email: String) {
        Api.retrofitService.sendEmail(email).enqueue(object : Callback<ForgetPasswordResponse> {
            override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<ForgetPasswordResponse>, response: Response<ForgetPasswordResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "已發送確認信至email", Toast.LENGTH_SHORT).show()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ForgetPasswordResponse>() {}.type
                    var errorResponse: ForgetPasswordResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(this@MainActivity, "${errorResponse.error}", Toast.LENGTH_SHORT).show()
                }
            }
        })
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
        btn_forgetPassword.visibility = View.VISIBLE
        btn_google_login.visibility = View.VISIBLE
    }
}