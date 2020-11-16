package com.example.cardtask.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import kotlinx.android.synthetic.main.task_model.view.*

//class RvTaskAdapter(private val clickListener: RvTaskAdapter.IClickListener) : RecyclerView.Adapter<RvTaskAdapter.ViewHolder>() {
class RvTaskAdapter() : RecyclerView.Adapter<RvTaskAdapter.ViewHolder>() {

    private val rvTaskList = mutableListOf<CardResponse.UserData.ShowCard.ShowTask>()
    var listener: (CardResponse.UserData.ShowCard.ShowTask) -> Unit = {}
    var longClickListener: (position: Int) -> Boolean = { true }
    var taskLongClick:(CardResponse.UserData.ShowCard.ShowTask) -> Boolean = { true }

//    fun setListener(mClickListener: IClickListener,mLongClickListener: ILongClickListener){
//        this.listener = mClickListener
//        this.longClickListener = mLongClickListener
//    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.ed_taskTitle
        val cvTask: ConstraintLayout = itemView.cv_task


//        init {
//            itemView.setOnClickListener {
////                clickListener.click(adapterPosition)
////                listener?.click(adapterPosition)
////                listener.invoke()
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val taskView = LayoutInflater.from(parent.context).inflate(
            R.layout.task_model, parent, false
        )
        return ViewHolder(taskView)
    }

    override fun getItemCount(): Int {
        return rvTaskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rvTaskList[position]
        var colorNum = R.color.origin
        colorNum = when (currentItem.tag) {
            "red" -> R.color.red
            "yellow" -> R.color.yellow
            "green" -> R.color.green
            "pink" -> R.color.pink
            "blue" -> R.color.blue
            else -> R.color.origin
        }
        holder.taskTitle.text = currentItem.title
        holder.cvTask.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorNum))
        holder.cvTask.setOnClickListener { listener.invoke(currentItem) }
        holder.cvTask.setOnLongClickListener {
            longClickListener.invoke(position)
            taskLongClick.invoke(rvTaskList[position])
        }

//        holder.cv.setOnClickListener { Toast.makeText(holder.cv.context, "In", Toast.LENGTH_SHORT).show() }   //Elvis
    }

    fun update(updateList: MutableList<CardResponse.UserData.ShowCard.ShowTask>) {
        rvTaskList.clear()
        rvTaskList.addAll(updateList)
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