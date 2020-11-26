package com.example.cardtask


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.cardtask.api.Api
import com.example.cardtask.api.CardResponse
import com.example.cardtask.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


val cardFragment = MainFragment()

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val pref = SharedPreferences(this)
        val token = pref.getData()!!

        Log.e("UI", "drawler1 $drawerLayout")
        Log.e("UI", "drawler2 ${drawerLayout.navigation}")
        Log.e("UI", "drawler3 ${drawerLayout.navigation.getHeaderView(0)}")
        Log.e("UI", "drawler4 ${drawerLayout.navigation.getHeaderView(0).btn_logout}")

        /** 這些是drawerLayout nav_header_mail中再下一層的view,要從最外層開始找*/
        val logOut = navigation.getHeaderView(0).findViewById<Button>(R.id.btn_logout)
        val userName = navigation.getHeaderView(0).findViewById<TextView>(R.id.tv_userName)
        val userImage = navigation.getHeaderView(0).findViewById<ImageView>(R.id.img_user)
        val userEmail = navigation.getHeaderView(0).findViewById<TextView>(R.id.tv_userEmail)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // Set the toolbar
        setSupportActionBar(toolbar)
        navigation.setNavigationItemSelectedListener {
            Toast.makeText(this, it.itemId.toString(), Toast.LENGTH_LONG).show()
            when (it.itemId) {
                R.id.action_home -> {
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show()
//                    drawerLayout.closeDrawers()
                    //用Handler收drawer,可以smooth一點點
                    Handler().postDelayed({ drawerLayout.closeDrawer(GravityCompat.START) }, 200)
                }
//                R.id.group1_1 -> {
//                    changeFragment(it.title.toString(), Group1_1Fragment())
//                    drawer.closeDrawers()
//                }
            }
            true
        }


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
                        if (res?.userData?.image.toString() != "null") {
                            Glide.with(this@SecondActivity)
                                .load("https://storage.googleapis.com/gcs.gill.gq/${res?.userData?.image.toString()}")
                                .transform(CircleCrop()).into(userImage)
                            userEmail.text = res?.userData?.email
                        }
//                        res?.userData?.showCards?.forEach { card ->
//                            MainFragment.cardList.add(card)
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

    //  覆寫 onOptionsItemSelected 漢堡選單按了才有功能
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            //漢堡選單原生的id,注意前要加上android才會找到正確的R class
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
//                Log.d("ham", "in")
            }
        }
//        Log.d("ham", "${item?.itemId}")
//        Log.d("ham", "${android.R.id.home}")
//        Log.d("ham", "${R.id.home}")

        return super.onOptionsItemSelected(item)
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