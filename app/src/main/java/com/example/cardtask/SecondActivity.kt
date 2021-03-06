package com.example.cardtask


import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.cardtask.api.Api
import com.example.cardtask.api.CardResponse
import com.example.cardtask.fragment.CardFragment
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val cardFragment = CardFragment()

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val pref = SharedPreferences(this)
        val token = pref.getData()!!
//        登出,清掉本地token,使用post,讓btn_logout這個button有時間產生
        Log.e("UI", "drawler1 $drawerLayout")
        Log.e("UI", "drawler2 ${drawerLayout.navigation}")
        Log.e("UI", "drawler3 ${drawerLayout.navigation.getHeaderView(0)}")
        Log.e("UI", "drawler4 ${drawerLayout.navigation.getHeaderView(0).btn_logout}")

        /** 這些是drawerLayout中再下一層的view,要從最外層開始找*/
        val logOut = navigation.getHeaderView(0).findViewById<Button>(R.id.btn_logout)
        val userName = navigation.getHeaderView(0).findViewById<TextView>(R.id.tv_userName)
        val userImage = navigation.getHeaderView(0).findViewById<ImageView>(R.id.img_user)

        logOut.setOnClickListener {
            pref.delete()
            Toast.makeText(this@SecondActivity, "log out click", Toast.LENGTH_SHORT).show()
            this@SecondActivity.finish()
            startActivity(Intent(this@SecondActivity, MainActivity::class.java))
        }

//        if (savedInstanceState == null) {
            Toast.makeText(this, "TOKEN = $token", Toast.LENGTH_SHORT).show()
            Api.retrofitService.getCard(token)
                .enqueue(object : Callback<CardResponse> {
                    override fun onFailure(call: Call<CardResponse>, t: Throwable) {
                        Log.e("getCard Failed", t.toString())
                    }
                    override fun onResponse(
                        call: Call<CardResponse>,
                        response: Response<CardResponse>
                    ) {
                        val res = response.body()
                        if (response.isSuccessful) {
                            userName.text = res?.userData?.username
                            Glide.with(this@SecondActivity)
                                .load("https://storage.googleapis.com/gcs.gill.gq/${res?.userData?.image.toString()}")
                                .into(userImage)
//                        res?.userData?.showCards?.forEach { card ->
//                            CardFragment.cardList.add(card)
//                        }
//                        Log.d("Success!", "getCard OK")
                        } else {    //token失效 ,啓動MainActivity重新登入
                            pref.delete()
                            this@SecondActivity.finish()
                            startActivity(Intent(this@SecondActivity, MainActivity::class.java))
                        }
                    }
                })
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, cardFragment, "cardFragment")
                commit()
            }
//        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers()
            } else return super.onKeyDown(keyCode, event)
            return true //回傳true表示消耗這個點擊事件,不再往下傳了
        }
        return super.onKeyDown(keyCode, event)
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