package com.example.cardtask.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardtask.R
import com.example.cardtask.api.*
import com.example.cardtask.fragment.MainFragment.Companion.cardList
import com.example.cardtask.fragment.MainFragment.Companion.groupCardList
import com.example.cardtask.goToEdTask
import com.example.cardtask.hideKeyboard
import com.example.cardtask.recyclerView.RvTaskAdapter
import com.example.cardtask.recyclerView.RvUsersAdapter
import com.example.cardtask.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_bottom_sheet.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_card.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardFragment : Fragment() {

    private lateinit var rootView: View

    lateinit var taskAdapter: RvTaskAdapter
    lateinit var usersAdapter: RvUsersAdapter
    lateinit var tasks: MutableList<CardResponse.UserData.ShowCard.ShowTask>
    private val newTaskRequestCode = 555
    private val edTaskRequestCode = 111
    private val cardRequestCode = 456

    var cardPosition = 0
    var cardId = 0
    var userList = mutableListOf<UserGroupResponse.UsersData>()
    var card = CardResponse.UserData.ShowCard()
    var isPrivate = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.getParcelable<CardResponse.UserData.ShowCard>("card")!!
            cardId = card.id
            isPrivate = card.private
            cardPosition = it.getInt("pos")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_card, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  背景圖用
        gradientChart_cardFragment.chartValues = arrayOf(
            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f
        )

        rootView.tv_EdCardId.text = cardId.toString()
        getCards()
        getUsers()

//        rootView.ed_cardName.setText(cardList[cardPosition].cardName)
        rootView.ed_cardName.setText(card.cardName)

//        產生task recyclerView
//        tasks = cardList[cardPosition].showTasks.toMutableList()
        tasks = card.showTasks.toMutableList()
        taskAdapter = RvTaskAdapter()

        rootView.ed_rvTask.adapter = taskAdapter    //將task recyclerview接上
            .apply {
                listener = { showTask ->
                    showToast("task item $showTask")
                    goToEdTask(showTask)
                }
                longClickListener = { position ->
                    delTask(position)
                    true
                }
//                setListener(object : RvTaskAdapter.IClickListener {
//                    override fun click(position: Int) {
//                        Toast.makeText(activity, "task item $position", Toast.LENGTH_SHORT).show()
//                    }
//                })
            }
        rootView.ed_rvTask.layoutManager = LinearLayoutManager(activity)

//  是否群組卡片
        if (!isPrivate) rootView.tv_groupCard.text = "群組卡片"

//  更新卡片名稱
        rootView.btn_cardUpdate.setOnClickListener {
            hideKeyboard(tv_EdCardId)
            cardRename()
        }

//  編輯使用者,開啓bottom sheet,建立user recyclerView
        rootView.btn_editUsers.setOnClickListener {

            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bsView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bottom_sheet, null)
            bottomSheetDialog.setContentView(bsView)
            val parent = bsView.parent as ViewGroup //取得BottomSheet介面設定
            parent.setBackgroundResource(android.R.color.transparent) //將背景設為透明，否則預設白底

//            (bsView.parent as ViewGroup?)?.background = ColorDrawable(Color.TRANSPARENT)
            bottomSheetDialog.show()
//          關閉bottom sheet
            bottomSheetDialog.btn_closeBottomSheet.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.btn_addUser.setOnClickListener {
                val email =AddUser(bottomSheetDialog.ed_userEmail.text.toString())
                addUser(email)
            }

            //        users recyclerView
            usersAdapter = RvUsersAdapter(this)
            bottomSheetDialog.rv_userOfCard.adapter = usersAdapter
            bottomSheetDialog.rv_userOfCard.layoutManager = LinearLayoutManager(activity)
            usersAdapter.update(userList)
        }
//  Fab新增Task,開啓task fragment
        newTaskFab()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == newTaskRequestCode || requestCode == edTaskRequestCode) {
            getCards()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //取得卡片的使用者
    private fun getUsers() {
        Api.retrofitService.getUsers(token, cardId)
            .enqueue(object : Callback<UserGroupResponse> {
                override fun onFailure(call: Call<UserGroupResponse>, t: Throwable) {
                    Log.e("getUsers Failed", t.toString())
                }

                override fun onResponse(call: Call<UserGroupResponse>, response: Response<UserGroupResponse>) {
                    if (response.isSuccessful) {
                        userList.clear()
                        val res = response.body()
                        Log.d("Success!", "$res")
                        res?.usersData?.forEach {
                            userList.add(it)
                        }
                    }
                }
            })
    }

    //取得所有資料
    private fun getCards() {
        Api.retrofitService.getCard(token)
            .enqueue(object : Callback<CardResponse> {
                override fun onFailure(call: Call<CardResponse>, t: Throwable) {
                    Log.e("getCard Failed", t.toString())
                }

                override fun onResponse(
                    call: Call<CardResponse>,
                    response: Response<CardResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("Success!", "getCard OK")
                        updateCards(response.body())
//                        displayTasksOfCard()

                        val intent = Intent()
                        intent.putExtra("pos", cardPosition)
                        targetFragment?.onActivityResult(cardRequestCode, Activity.RESULT_OK, intent)
                    }
                }
            })
    }

    private fun updateCards(res: CardResponse?) {
        cardList.clear()
        groupCardList.clear()
        res?.userData?.showCards?.forEach { card ->
            if (card.id == this.cardId) {  //新增task後,檢查此張card id,並更新顯示之task
                this.card = card
                tasks = card.showTasks.toMutableList()
                displayTasksOfCard()
            }

            when (card.private) {
                true -> cardList.add(card)
                else -> groupCardList.add(card)
            }
        }

    }

    private fun displayTasksOfCard() {
//        taskAdapter.update(cardList[cardPosition].showTasks.toMutableList())
        taskAdapter.update(card.showTasks.toMutableList())
//        taskAdapter.update(tasks)
    }


    private fun cardRename() {
        val newCard = NewCard()
        newCard.cardName = ed_cardName.text.toString()
        Api.retrofitService.updateCard(token, cardId, newCard)
            .enqueue(object : MyCallback<UpdateCardResponse>() {
                override fun onSuccess(call: Call<UpdateCardResponse>, response: Response<UpdateCardResponse>) {
                    showToast("卡片已更名爲 [${newCard.cardName}] ")
                    Log.d("Success!", "Update Card OK")
                    getCards()
                }
            })
    }

    private fun newTaskFab() {   //新增task,轉到task頁面,add NewTaskFragment
        btn_fabTask.setOnClickListener {
            val taskFragment = NewTaskFragment()
            taskFragment.arguments = Bundle().apply {
                putInt("pos", cardPosition)
                putInt("id", cardId)
            }
            taskFragment.setTargetFragment(this, newTaskRequestCode)
            requireFragmentManager().beginTransaction()?.apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                add(R.id.frame_layout, taskFragment)
                addToBackStack("edCardFragment")
                commit()
            }
        }
    }

    private fun delTask(position: Int) {     //刪除task
        val delBuild = AlertDialog.Builder(activity)

        delBuild
            .setTitle("確認要刪除Task [${tasks[position].title}]?")
            .setPositiveButton("刪除") { _, _ ->
                Api.retrofitService.deleteTask(token, tasks[position].id)
                    .enqueue(object : MyCallback<DeleteTaskResponse>() {
                        override fun onSuccess(
                            call: Call<DeleteTaskResponse>, response: Response<DeleteTaskResponse>
                        ) {
                            cardList.forEach {
                                it.showTasks.remove(tasks[position])
                            }
                            groupCardList.forEach {
                                it.showTasks.remove(tasks[position])
                            }
                            tasks.removeAt(position)
                            taskAdapter.update(tasks)
                            // 刪除task也要通知MainFragment更新
                            val intent = Intent()
//                            intent.putExtra("pos", cardPosition)
                            targetFragment?.onActivityResult(cardRequestCode, Activity.RESULT_OK, intent)
                        }
                    })
            }
            .setNegativeButton("取消") { _, _ ->
            }
            .show()
    }

    private fun addUser(email: AddUser){
        Api.retrofitService.addUser(token,cardId,email)
            .enqueue(object :MyCallback<AddUserResponse>(){
                override fun onSuccess(call: Call<AddUserResponse>, response: Response<AddUserResponse>) {
                    getUsers()
                    showToast("使用者已加入")
                }
            })
    }
}