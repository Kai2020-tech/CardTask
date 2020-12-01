package com.example.cardtask.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import kotlinx.android.synthetic.main.model_search_result.view.*

class RvSearchResultAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val rvSearchResultList = mutableListOf<Any>()
    private val rvTaskResultList = mutableListOf<CardResponse.UserData.ShowCard.ShowTask>()

    var listener: (CardResponse.UserData.ShowCard.ShowTask) -> Unit = {}
    var longClickListener: (position: Int) -> Boolean = { true }
    var taskLongClick: (CardResponse.UserData.ShowCard.ShowTask) -> Boolean = { true }

//    fun setListener(mClickListener: IClickListener,mLongClickListener: ILongClickListener){
//        this.listener = mClickListener
//        this.longClickListener = mLongClickListener
//    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.tv_resultTitle
        val cardType: TextView = itemView.tv_resultType


    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.tv_resultTitle
        val taskType: TextView = itemView.tv_resultType

    }

//        init {
//            itemView.setOnClickListener {
////                clickListener.click(adapterPosition)
////                listener?.click(adapterPosition)
////                listener.invoke()
//            }
//        }


    override fun getItemViewType(position: Int): Int {
//        val list = rvCardResultList.flatMap { it.showTasks}
        return if (rvSearchResultList[position] is CardResponse.UserData.ShowCard) 1
        else 2 //if (rvSearchResultList[position] is CardResponse.UserData.ShowCard.ShowTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1) {
            val cardView = LayoutInflater.from(parent.context).inflate(
                R.layout.model_search_result, parent, false
            )
            CardViewHolder(cardView)
        } else {
            val taskView = LayoutInflater.from(parent.context).inflate(
                R.layout.model_search_result, parent, false
            )
            TaskViewHolder(taskView)
        }


    }

    override fun getItemCount(): Int {
        return rvSearchResultList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = rvSearchResultList[position]

        if (holder is CardViewHolder ) {
//            listener.click("typeTask")
            holder.cardType.text = (currentItem as CardResponse.UserData.ShowCard).type
            holder.cardTitle.text = (currentItem as CardResponse.UserData.ShowCard).cardName

        } else if(holder is TaskViewHolder){
//            listener.click("typeCard")
            holder.taskType.text = (currentItem as CardResponse.UserData.ShowCard.ShowTask).type
//            holder.resultType.text = "任務"
            holder.taskTitle.text = (currentItem as CardResponse.UserData.ShowCard.ShowTask).title
        }


//        holder.taskTitle.text = currentItem.title
////        holder.cvTask.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorNum))
//        holder.taskColor.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorNum))
//        holder.cvTask.setOnClickListener { listener.invoke(currentItem) }
//        holder.cvTask.setOnLongClickListener {
//            //用於card fragment
//            longClickListener.invoke(position)
//            //用於main fragment
//            taskLongClick.invoke(rvResultList[position])
//        }

//        holder.cv.setOnClickListener { Toast.makeText(holder.cv.context, "In", Toast.LENGTH_SHORT).show() }   //Elvis
    }

    fun update(updateList: MutableList<Any>) {
        rvSearchResultList.clear()
        rvSearchResultList.addAll(updateList)
        notifyDataSetChanged()
    }

//    interface ILongClickListener {
//        fun longClick(position: Int)
//    }
//
//    interface IClickListener {
//        fun click(position: Int)
//    }


}