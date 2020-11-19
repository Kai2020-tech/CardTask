package com.example.cardtask.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import kotlinx.android.synthetic.main.model_task.view.*

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
            R.layout.model_task, parent, false
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
            "orange" -> R.color.orange
            "yellow" -> R.color.yellow
            "green" -> R.color.green
            "blue" -> R.color.blue
            "darkBlue" -> R.color.darkBlue
            "purple" -> R.color.purple
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