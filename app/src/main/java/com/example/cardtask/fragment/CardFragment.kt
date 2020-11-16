package com.example.cardtask.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.cardtask.*
import com.example.cardtask.api.*
import com.example.cardtask.recyclerView.RvCardAdapter
import kotlinx.android.synthetic.main.dialog_new_card.view.*
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_card.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CardFragment : Fragment()
//    ,RvCardAdapter.IClickListener
//    ,RvCardAdapter.ILongClickListener
{
    private lateinit var cardAdapter: RvCardAdapter
    private lateinit var rootView: View

    private val edTaskRequestCode = 111
    private val cardRequestCode = 456

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_card, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val perf = SharedPreferences(requireContext())
        token = perf.getData() ?: ""

        getCards()
        cardAdapter = RvCardAdapter()
        rootView.rv_card.adapter = cardAdapter.apply {
            setCardClickListener(object : RvCardAdapter.IClickListener {
                override fun click(position: Int) {     //點擊卡片,到編輯頁面,add EdCareFragment
                    showToast("Item $position clicked")
                    goToEdCard(position)
                }
            }, object : RvCardAdapter.ILongClickListener {
                override fun longClick(position: Int) { //長點擊卡片,出現刪除卡片對話窗
                    showToast("Item $position clicked")
                    delCard(position)
                }
            })

            taskClickListener = { showTask ->           //點擊task,到編輯task頁面,add EdTaskFragment
                Log.i("card fragment", "showTask")
                showToast("task item $showTask")
                goToEdTask(showTask)

            }
            taskLongClickListener = { showTask ->       //長點擊Task,出現刪除Task對話窗
                delTask(showTask)
                true
                /*if(){ true} else{ false}*/
            }

//            val (clickListener, longClickListener) = generateRvCardAdapterListener()
//            setCardClickListener(clickListener, longClickListener)
        }

        setRvLayout()

        newCardFab()

        println("***************** onViewCreated $this")
//        update()    //目標TaskFragment有更新時,呼叫update()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("***************** onDestroyView $this")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cardRequestCode || requestCode == edTaskRequestCode) {
            cardAdapter.update(cardList)
//            val pos = data?.getIntExtra("pos", 0) ?: return
//            cardAdapter.notifyItemChanged(pos)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*** 小魚教的listener方式*/
/*    private fun generateRvCardAdapterListener(): Pair<RvCardAdapter.IClickListener, RvCardAdapter.ILongClickListener> {
        return Pair(object : RvCardAdapter.IClickListener {
            override fun click(position: Int) {
                Toast.makeText(activity, "Item $position clicked", Toast.LENGTH_SHORT).show()
                val edCardFragment = EdCardFragment()
                edCardFragment.position = position
                edCardFragment.cardId = cardList[position].id

                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.frame_layout, edCardFragment)
                    addToBackStack("edCardFragment")
                    commit()
                }

            }

            override fun taskClick(position: Int) {
                Toast.makeText(activity, "task item $position", Toast.LENGTH_SHORT).show()
            }


        },
            object : RvCardAdapter.ILongClickListener {
                override fun longClick(position: Int) {
                    Toast.makeText(activity, "Item $position clicked", Toast.LENGTH_SHORT).show()
                    val delBuild = AlertDialog.Builder(activity)
                    val cardName = cardList[position].cardName
                    val cardId = cardList[position].id
                    delBuild
                        .setTitle("確認要刪除卡片 [$cardName]?")
                        .setPositiveButton("刪除") { _, _ ->
                            Api.retrofitService.deleteCard(token, cardId)
                                .enqueue(object : Callback<DeleteCardResponse> {
                                    override fun onFailure(
                                        call: Call<DeleteCardResponse>,
                                        t: Throwable
                                    ) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onResponse(
                                        call: Call<DeleteCardResponse>,
                                        response: Response<DeleteCardResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            showToast("[$cardName] 已刪除")
                                            Log.d("Success!", "Delete Card OK")
                                        }
                                    }
                                })
                            cardList.removeAt(position)
                            cardAdapter.update(cardList)
                        }
                        .setNegativeButton("取消") { _, _ ->
                        }
                        .show()
                }

            })
    }*/

    companion object {
        val cardList = mutableListOf<CardResponse.UserData.ShowCard>()
        val cameraRequestCode = 100
        val albumRequestCode = 200
    }

    private fun getCards() {    //取得所有資料
        Api.retrofitService.getCard(token)
            .enqueue(object : MyCallback<CardResponse>() {
                override fun onSuccess(call: Call<CardResponse>, response: Response<CardResponse>) {
                    val res = response.body()
                    updateCards(res)
                    displayCard()
                    Log.d("Success!", "getCard OK")
                }
            })
    }

    private fun updateCards(res: CardResponse?) {
        cardList.clear()
        res?.userData?.showCards?.forEach { card ->
            cardList.add(card)
        }
    }

    private fun displayCard() {
        cardAdapter.update(cardList)
    }

    private fun getCardsScrollToPosition() {  //取得所有資料後,捲動到最新的卡片
        Api.retrofitService.getCard(token)
            .enqueue(object : MyCallback<CardResponse>() {
                override fun onSuccess(call: Call<CardResponse>, response: Response<CardResponse>) {
                    val res = response.body()
                    cardList.clear()
                    res?.userData?.showCards?.forEach { card ->
                        cardList.add(card)
                        Log.d("card", "$cardList")
                    }
                    Log.d("Success!", "getCard OK")
                    cardAdapter.update(cardList)
                    rootView.rv_card.smoothScrollToPosition(cardList.size)
                }
            })
    }

    private fun goToEdCard(position: Int) {
        val edCardFragment = EdCardFragment()
//                    fragment傳遞資料要使用系統的arguments
        edCardFragment.arguments = Bundle().apply {
            putInt("pos", position)
            putInt("id", cardList[position].id)
        }
        /**將此fragment設爲target,接受回來的資料*/
        edCardFragment.setTargetFragment(this, cardRequestCode)

        requireFragmentManager().beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            add(R.id.frame_layout, edCardFragment)
            addToBackStack("edCardFragment")
            commit()
        }
    }

    private fun delCard(position: Int) {
        val delBuild = AlertDialog.Builder(activity)
        val cardName = cardList[position].cardName
        val cardId = cardList[position].id
        delBuild
            .setTitle("確認要刪除卡片 [$cardName]?")
            .setPositiveButton("刪除") { _, _ ->
                Api.retrofitService.deleteCard(token, cardId)
                    .enqueue(object : MyCallback<DeleteCardResponse>() {
                        override fun onSuccess(call: Call<DeleteCardResponse>, response: Response<DeleteCardResponse>) {
                            showToast("卡片 [$cardName] 已刪除")
                            Log.d("Success!", "Delete Card OK")
                            cardList.removeAt(position)
                            cardAdapter.update(cardList)
                        }
                    })
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun delTask(showTask: CardResponse.UserData.ShowCard.ShowTask) {
        val delBuild = AlertDialog.Builder(activity)
        delBuild.setTitle("確認要刪除Task [${showTask.title}]?")
            .setPositiveButton("刪除") { _, _ ->
                Api.retrofitService.deleteTask(token, showTask.id)
                    .enqueue(object : MyCallback<DeleteTaskResponse>() {
                        override fun onSuccess(
                            call: Call<DeleteTaskResponse>, response: Response<DeleteTaskResponse>
                        ) {
                            cardList.forEach {
                                it.showTasks.remove(showTask)
                            }
                            cardAdapter.update(cardList)
                        }
                    })
            }
            .setNegativeButton("取消") { _, _ ->
            }
            .show()
    }

    private fun setRvLayout() {     //將recyclerview接上,設置佈局水平方式
        rootView.rv_card.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(rootView.rv_card)     //recyclerview捲動停止時,可以讓item位於畫面中間
    }

    private fun newCardFab() {
        val newCardDialog = LayoutInflater.from(activity).inflate(R.layout.dialog_new_card, null)
        btn_fabCard.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(activity)
            val dialog = dialogBuilder.setView(newCardDialog)
                .setView(newCardDialog)
                .setOnDismissListener {
                    (newCardDialog.parent as ViewGroup).removeView(newCardDialog)
                }
                .show()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) //set dialog background transparent

            newCardDialog.btn_cardConfirm.setOnClickListener {
                val newCard = NewCard()

                newCard.cardName = newCardDialog.ed_newTask.text.toString()

                Api.retrofitService.newCard(token, newCard)
                    .enqueue(object : MyCallback<NewCardResponse>() {
                        override fun onSuccess(call: Call<NewCardResponse>, response: Response<NewCardResponse>) {
                            Log.d("Success!", "newCard OK")
                            getCardsScrollToPosition()
                        }
                    })

                dialog.dismiss()
            }
            newCardDialog.btn_cardCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}