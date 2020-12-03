package com.example.cardtask.recyclerView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import kotlinx.android.synthetic.main.model_search_result.view.*
import kotlinx.android.synthetic.main.model_task.view.*

class RvSearchResultAdapter() : RecyclerView.Adapter<RvSearchResultAdapter.ResultViewHolder>() {

    private val rvSearchResultList = mutableListOf<SearchResultItem>()
    var resultClickListener: (SearchResultItem) -> Unit = {}
    var resultLongClickListener: (SearchResultItem) -> Boolean = { true }

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultItem: ConstraintLayout = itemView.cv_searchResultItem
        val title: TextView = itemView.tv_resultTitle
        val type: TextView = itemView.tv_resultType
        val taskColor: ImageView = itemView.img_taskColor2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val resultItem = LayoutInflater.from(parent.context).inflate(
            R.layout.model_search_result, parent, false
        )
        return ResultViewHolder(resultItem)
    }

    override fun getItemCount(): Int {
        return rvSearchResultList.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentItem = rvSearchResultList[position]
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

        holder.title.text = currentItem.title
//        holder.type.text = currentItem.type
        when(currentItem.type){
            "task" -> {
                holder.taskColor.visibility= View.VISIBLE
                holder.type.visibility = View.GONE
                holder.taskColor.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorNum))
            }
            "card" ->{
                if(currentItem.isPrivate==true){
                    holder.type.text = "個人卡片"
                    holder.resultItem.setBackgroundColor(Color.parseColor("#FFE0B2"))
                }else{
                    holder.type.text = "群組卡片"
                    holder.resultItem.setBackgroundColor(Color.parseColor("#FFAB91"))
                }
            }
        }

        holder.resultItem.setOnClickListener {
            resultClickListener.invoke(currentItem)
        }

        holder.resultItem.setOnLongClickListener {
            resultLongClickListener.invoke(currentItem)
        }
    }

    fun update(updateList: MutableList<SearchResultItem>) {
        rvSearchResultList.clear()
        rvSearchResultList.addAll(updateList)
        notifyDataSetChanged()
    }
}
data class SearchResultItem(
    val title: String,
    val id: Int,
    val type: String,   //card -> cardType , task -> taskType
    val tag: String?,
    val isPrivate: Boolean?
)