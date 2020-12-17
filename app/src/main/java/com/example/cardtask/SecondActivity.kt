package com.example.cardtask


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.cardtask.api.Api
import com.example.cardtask.api.CardResponse
import com.example.cardtask.api.token
import com.example.cardtask.fragment.MainFragment
import com.example.cardtask.fragment.SearchResultFragment
import com.example.cardtask.fragment.UserSettingFragment
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//val mainFragment = MainFragment()

class SecondActivity : AppCompatActivity(), IObserver {

    companion object {
        val totalCardList = mutableListOf<CardResponse.UserData.ShowCard>()
    }

    //    private val userSettingFragment = UserSettingFragment()
    lateinit var userImage: ImageView
    lateinit var userName: TextView
    lateinit var userEmail: TextView
    var arUserPhotoPath: String = ""
    var arUserName: String = ""
    var arUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val pref = SharedPreferences(this)
        val token = pref.getData()!!
        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mainFragment, mainFragment::class.java.name)
            commit()
        }

        Log.e("UI", "drawer1 $drawerLayout")
        Log.e("UI", "drawer2 ${drawerLayout.navigation}")
        Log.e("UI", "drawer3 ${drawerLayout.navigation.getHeaderView(0)}")
        Log.e("UI", "drawer4 ${drawerLayout.navigation.getHeaderView(0).btn_logout}")

        /** 這些是drawerLayout nav_header_mail中再下一層的view,要從最外層開始找*/
        val logOut = navigation.getHeaderView(0).findViewById<Button>(R.id.btn_logout)
        userName = navigation.getHeaderView(0).findViewById<TextView>(R.id.tv_userName)
        userImage = navigation.getHeaderView(0).findViewById<ImageView>(R.id.img_userPhoto)
        userEmail = navigation.getHeaderView(0).findViewById<TextView>(R.id.tv_userEmail)

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
                        totalCardList.clear()
                        userName.text = res?.userData?.username
                        if (res?.userData?.image.toString() != "null") {
                            val userPhotoPath =
                                if (res?.userData?.image.toString().contains("googleusercontent")) {
                                    res?.userData?.image.toString() //如果有取得google帳號的圖片路徑,就用這
                                } else {
                                    "https://storage.googleapis.com/gcs.gill.gq/${res?.userData?.image.toString()}"
                                }
                            Glide.with(this@SecondActivity).load(userPhotoPath).transform(CircleCrop()).into(userImage)
                            userEmail.text = res?.userData?.email
                            arUserPhotoPath = userPhotoPath
                            arUserEmail = res?.userData?.email.toString()
                            arUserName = res?.userData?.username.toString()
                        }
                        res?.userData?.showCards?.forEach { card ->
                            totalCardList.add(card)
                        }
                        Log.d("Success!", "get Total Card List OK")
                    } else {    //token失效 ,啓動MainActivity重新登入
                        pref.delete()
                        this@SecondActivity.finish()
                        startActivity(Intent(this@SecondActivity, MainActivity::class.java))
                    }
                }
            })

        //以下側邊選單內容
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // Set the toolbar
        setSupportActionBar(toolbar)

        //側邊各item的點擊監聽
        navigation.setNavigationItemSelectedListener {
//            Toast.makeText(this, it.itemId.toString(), Toast.LENGTH_LONG).show()
            when (it.itemId) {
                R.id.action_home -> {
                    val mainFragment = MainFragment()

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, mainFragment)
                        commit()
                    }
                    //用Handler收drawer,可以smooth一點點
//                    Handler().postDelayed({ drawerLayout.closeDrawer(GravityCompat.START) }, 200)
                    drawerLayout.closeDrawers()
                }

                R.id.action_settings -> {
                    val userSettingFragment = UserSettingFragment()
                    userSettingFragment.arguments = Bundle().apply {
                        putString("userPhotoPath", arUserPhotoPath)
                        putString("userEmail", arUserEmail)
                        putString("userName", arUserName)
                    }
                    userSettingFragment.addSubscriber(this)
                    supportFragmentManager.popBackStack(userSettingFragment::class.java.name, POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, userSettingFragment, userSettingFragment::class.java.name)
                        addToBackStack(userSettingFragment::class.java.name)
                        commit()
                    }
                    drawerLayout.closeDrawers()
                }

                R.id.action_help -> {
                    drawerLayout.closeDrawers()
                    Toast.makeText(this, "右下+號開始使用吧", Toast.LENGTH_SHORT).show()
                }

                R.id.action_about -> {
                    drawerLayout.closeDrawers()
                    Toast.makeText(this, "android: Kai \n backend: Gill", Toast.LENGTH_SHORT).show()
                }

                R.id.btn_logOut -> {
                    pref.delete()
                    Toast.makeText(this@SecondActivity, "log out click", Toast.LENGTH_SHORT).show()
                    this@SecondActivity.finish()
                    startActivity(Intent(this@SecondActivity, MainActivity::class.java))
                }
            }
            true
        }
        //以上側邊選單內容

        //登出功能改爲在/menu/drawer的item實作
//        logOut.setOnClickListener {
//            pref.delete()
//            Toast.makeText(this@SecondActivity, "log out click", Toast.LENGTH_SHORT).show()
//            this@SecondActivity.finish()
//            startActivity(Intent(this@SecondActivity, MainActivity::class.java))
//        }

        Toast.makeText(this, "TOKEN = $token", Toast.LENGTH_SHORT).show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    //  覆寫 onOptionsItemSelected 漢堡選單按了才有功能
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            //漢堡選單原生的id,注意前要加上android才會找到正確的R class
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
//        Log.d("ham", "${item?.itemId}")
//        Log.d("ham", "${android.R.id.home}")
//        Log.d("ham", "${R.id.home}")

        return super.onOptionsItemSelected(item)
    }

    //tool bar的search menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val settingItem = menu?.findItem(R.id.action_settings)
        //setting選單
        settingItem?.setOnMenuItemClickListener {
            Toast.makeText(this@SecondActivity, "setting clicked", Toast.LENGTH_SHORT).show()
            return@setOnMenuItemClickListener true
        }

        //search功能在這
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "請輸入卡片或任務名稱"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                //當search文字輸入完後,按下軟鍵盤搜尋鍵時的動作
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchView.windowToken, 0)  //搜尋後收鍵盤

                    val cardResultList = arrayListOf<CardResponse.UserData.ShowCard>()
                    val taskResultList = arrayListOf<CardResponse.UserData.ShowCard.ShowTask>()

//                    val groupCardResultList = mutableListOf<CardResponse.UserData.ShowCard>()
                    if (!query.isNullOrEmpty()) {
                        totalCardList.forEach { card ->
                            if (card.cardName.contains(query)) {
//                                card.showTasks.clear()
                                cardResultList.add(card)
                            }
                            card.showTasks.forEach {
                                if (it.title.contains(query)) {
                                    taskResultList.add(it)
                                }
                            }
                        }
                    }

                    if (cardResultList.isNotEmpty() || taskResultList.isNotEmpty()) {
//                        searchItem.collapseActionView()
                        val searchResultFragment = SearchResultFragment()

                        searchResultFragment.arguments = Bundle().apply {
                            putParcelableArrayList("taskList", taskResultList)  //不能放ArrayList<Any>型態的物件進去
                            putParcelableArrayList("cardList", cardResultList)
                            putString("queryString", query)
                        }

                        searchView.onActionViewCollapsed() //搜尋完後,將toolbar恢復原樣

                        supportFragmentManager.popBackStack("searchResultFragment", POP_BACK_STACK_INCLUSIVE)
                        supportFragmentManager.beginTransaction().apply {
                            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                            replace(R.id.frame_layout, searchResultFragment, "searchResultFragment")
                            addToBackStack("searchResultFragment")
                            commit()
                        }

                    } else {
                        Toast.makeText(this@SecondActivity, "$query 查無資料", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }

                //當search text改變時,即時的動作
                override fun onQueryTextChange(newText: String?): Boolean {
//                    if (newText!!.isNotEmpty()) {
//                        //這裡沒用到
//                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
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

    override fun update() {
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
                        if (res?.userData?.image.toString() != "null") {
                            val userPhotoPath =
                                if (res?.userData?.image.toString().contains("googleusercontent")) {
                                    res?.userData?.image.toString() //如果有取得google帳號的圖片路徑,就用這
                                } else {
                                    "https://storage.googleapis.com/gcs.gill.gq/${res?.userData?.image.toString()}"
                                }
                            Glide.with(this@SecondActivity).load(userPhotoPath).transform(CircleCrop()).into(userImage)
                            userName.text = res?.userData?.username
                            userEmail.text = res?.userData?.email
                            arUserPhotoPath = userPhotoPath
                            arUserEmail = res?.userData?.email.toString()
                            arUserName = res?.userData?.username.toString()
                        }
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