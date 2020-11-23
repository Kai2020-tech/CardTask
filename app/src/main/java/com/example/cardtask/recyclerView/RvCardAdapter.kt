package com.example.cardtask.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cardtask.R
import com.example.cardtask.api.CardResponse
import kotlinx.android.synthetic.main.model_card.view.*

class RvCardAdapter() : RecyclerView.Adapter<RvCardAdapter.ViewHolder>() {

    private val rvCardList = mutableListOf<CardResponse.UserData.ShowCard>()
    private var cardClick: IClickListener? = null
    private var cardLongClick: ILongClickListener? = null

    var clickCard: (CardResponse.UserData.ShowCard) -> Unit = {}
    var longClickCard: (CardResponse.UserData.ShowCard) -> Boolean = { true }

    var taskClickListener: (CardResponse.UserData.ShowCard.ShowTask) -> Unit = {}
    var taskLongClickListener: (CardResponse.UserData.ShowCard.ShowTask) -> Boolean = { true }

    fun setCardClickListener(mClickListener: IClickListener, mLongClickListener: ILongClickListener) {
        this.cardClick = mClickListener
        this.cardLongClick = mLongClickListener
    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)     //這裏的onClick與onLongClick是分別二種做法
        , View.OnLongClickListener
//        ,RvTaskAdapter.IClickListener /*** 要實作RvTaskAdapter.IClickListener*/
    {
        val cvCard: ConstraintLayout = itemView.cv_card
        val cardName: TextView = itemView.tv_cardTitle
        val cardId: TextView = itemView.tv_cardId
        val cardUpdateTime: TextView = itemView.tv_update_time
        private val rvTask: RecyclerView? = itemView.findViewById<RecyclerView>(R.id.rv_task)
        private val taskAdapter: RvTaskAdapter = RvTaskAdapter()/*.apply {
            setListener(object : RvTaskAdapter.IClickListener{
                override fun click(position: Int) {
                    clickListener?.taskClick(position)
                }
            })

        }*/
            .apply {
                listener = taskClickListener
                taskLongClick = { showTask ->
                    taskLongClickListener.invoke(showTask)
                }
            }

        init {
            val context = itemView.context
            rvTask?.adapter = taskAdapter
            rvTask?.layoutManager = LinearLayoutManager(context)
            itemView.setOnClickListener {
                cardClick?.click(adapterPosition)
            }
            itemView.setOnLongClickListener(this)
        }

        fun bind(list: MutableList<CardResponse.UserData.ShowCard.ShowTask>) {
            taskAdapter.update(list)
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                cardLongClick?.longClick(position)
            }
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(
            R.layout.model_card, parent, false
        )
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return rvCardList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rvCardList[position]
        val underLineText = "<b><u>${currentItem.cardName}</>"   //控制文字樣式b粗體,i斜體,u底線
        val updateTime = currentItem.updatedAt.split("T", ".")
        holder.cardName.text = HtmlCompat.fromHtml(
            underLineText,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.cardId.text = currentItem.id.toString()

        holder.cardUpdateTime.text = "${updateTime[0]}\n${updateTime[1]}"
        holder.bind(rvCardList[position].showTasks.toMutableList())

        holder.cvCard.setOnClickListener { clickCard.invoke(currentItem) }
        holder.cvCard.setOnLongClickListener { longClickCard.invoke(currentItem) }
    }

    fun update(updateCard: MutableList<CardResponse.UserData.ShowCard>) {
        rvCardList.clear()
        rvCardList.addAll(updateCard)

        notifyDataSetChanged()
    }

    interface ILongClickListener {
        fun longClick(position: Int)
    }

    interface IClickListener {
        fun click(position: Int)
//        fun taskClick(position: Int)
    }
}

