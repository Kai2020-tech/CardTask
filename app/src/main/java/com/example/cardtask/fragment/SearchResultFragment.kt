package com.example.cardtask.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardtask.R
import com.example.cardtask.api.*
import com.example.cardtask.goToCard
import com.example.cardtask.goToEdTask
import com.example.cardtask.recyclerView.RvSearchResultAdapter
import com.example.cardtask.recyclerView.SearchResultItem
import com.example.cardtask.showToast
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rootView: View
    private lateinit var cardResultList: ArrayList<CardResponse.UserData.ShowCard>
    private lateinit var taskResultList: ArrayList<CardResponse.UserData.ShowCard.ShowTask>
    private lateinit var resultItemList: MutableList<SearchResultItem>
    private lateinit var queryString: String
    private val searchResultAdapter = RvSearchResultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        cardResultList = arguments?.getParcelableArrayList<CardResponse.UserData.ShowCard>("cardList")
                as ArrayList<CardResponse.UserData.ShowCard>

        taskResultList = arguments?.getParcelableArrayList<CardResponse.UserData.ShowCard.ShowTask>("taskList")
                as ArrayList<CardResponse.UserData.ShowCard.ShowTask>

        queryString = arguments?.getString("queryString").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        return rootView
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  背景圖用
        gradientChart_searchResultFragment.chartValues = arrayOf(
            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f
        )

        Log.d("list", "card $cardResultList")
        Log.d("list", "task $taskResultList")
        //加入search result的card
        resultItemList = cardResultList.map {
            SearchResultItem(
                title = it.cardName,
                id = it.id,
                type = it.type,
                tag = null,
                isPrivate = it.private
            )
        }.toMutableList()
        //加入search result的task
        resultItemList.addAll(taskResultList.map {
            SearchResultItem(
                title = it.title,
                id = it.id,
                type = it.type,
                tag = it.tag,
                isPrivate = null
            )
        })
        print("HHH $resultItemList")
        Log.d("HHH", "$resultItemList")

        rootView.rv_searchResult.adapter = searchResultAdapter
        rootView.rv_searchResult.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        searchResultAdapter.update(resultItemList)

        searchResultAdapter.resultClickListener = { searchResultItem ->
            showToast("$searchResultItem")
            when (searchResultItem.type) {
                "task" -> {
                    taskResultList.forEach {
                        if (it.id == searchResultItem.id) goToEdTask(it)
                    }
                }
                "card" -> {
                    cardResultList.forEach {
                        if (it.id == searchResultItem.id) goToCard(it)
                    }
                }
            }
        }

        searchResultAdapter.resultLongClickListener = { searchResultItem ->
            when (searchResultItem.type) {
                "task" -> {
                    taskResultList.forEach {
                        if (it.id == searchResultItem.id) delTask(searchResultItem)
                    }
                    true
                }
                "card" -> {
                    cardResultList.forEach {
                        if (it.id == searchResultItem.id) delCard(searchResultItem)
                    }
                    true
                }
                else -> true
            }
        }

        rootView.tv_searchResult.text = "'$queryString' 的搜尋結果,共有${resultItemList.size}筆"

    }

    private fun delCard(card: SearchResultItem) {
        val delBuild = AlertDialog.Builder(activity, R.style.delCardDialogTheme)  //自訂dialog背景色
        val cardName = card.title
        val cardId = card.id
        delBuild
            .setTitle("確認要刪除卡片 [$cardName]?")
            .setPositiveButton("刪除") { _, _ ->
                Api.retrofitService.deleteCard(token, cardId)
                    .enqueue(object : MyCallback<DeleteCardResponse>() {
                        override fun onSuccess(call: Call<DeleteCardResponse>, response: Response<DeleteCardResponse>) {
                            showToast("卡片 [$cardName] 已刪除")
                            Log.d("Success!", "Delete Card OK")
                            resultItemList.remove(card)
                            searchResultAdapter.update(resultItemList)
                        }
                    })
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun delTask(task: SearchResultItem) {
        val delBuild = AlertDialog.Builder(activity)
        delBuild.setTitle("確認要刪除Task [${task.title}]?")
            .setPositiveButton("刪除") { _, _ ->
                Api.retrofitService.deleteTask(token, task.id)
                    .enqueue(object : MyCallback<DeleteTaskResponse>() {
                        override fun onSuccess(
                            call: Call<DeleteTaskResponse>, response: Response<DeleteTaskResponse>
                        ) {
                            showToast("Task [${task.title}] 已刪除")
                            resultItemList.remove(task)
                            searchResultAdapter.update(resultItemList)
                        }
                    })
            }
            .setNegativeButton("取消") { _, _ ->
            }
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}