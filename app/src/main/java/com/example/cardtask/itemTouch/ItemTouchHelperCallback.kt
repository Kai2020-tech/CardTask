package com.example.cardtask.itemTouch

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(private val mAdapter: IthHelperInterface,private val cardId:Int) : ItemTouchHelper.Callback() {

    override fun isItemViewSwipeEnabled(): Boolean {    //首先開啓item滑動
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        //設定可往上及往下拖曳
//        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        //設定可往左及往右滑動
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        //若不想要其中一個功能就改成ACTION_STATE_IDLE
        val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    //  上下移動
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    //  左右滑動
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition,cardId)
    }
}

interface IthHelperInterface {  //讓recyclerView implement這個interface
    fun onItemMove(fromPosition: Int, toPosition: Int)  //上下
    fun onItemDismiss(position: Int,cardId: Int)    //左右
}