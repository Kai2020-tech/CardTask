package com.example.cardtask.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import com.example.cardtask.recyclerView.RvSearchResultAdapter
import com.example.cardtask.showToast
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*

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
    private var totalResultList = arrayListOf<Any>()
    private val searchResultAdapter = RvSearchResultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        cardResultList =
            arguments?.getParcelableArrayList<CardResponse.UserData.ShowCard>("cardList") as ArrayList<CardResponse.UserData.ShowCard>

        taskResultList =
            arguments?.getParcelableArrayList<CardResponse.UserData.ShowCard.ShowTask>("taskList") as ArrayList<CardResponse.UserData.ShowCard.ShowTask>

        totalResultList.clear()
        totalResultList.addAll(cardResultList)
        totalResultList.addAll(taskResultList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  背景圖用
        gradientChart_searchResultFragment.chartValues = arrayOf(
            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f
        )

        Log.d("list", "card $cardResultList")
        Log.d("list", "task $taskResultList")

        rootView.rv_searchResult.adapter = searchResultAdapter
        rootView.rv_searchResult.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        searchResultAdapter.update(totalResultList)


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