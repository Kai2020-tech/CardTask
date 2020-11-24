package com.example.cardtask.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.cardtask.R
import com.example.cardtask.api.UserGroupResponse
import com.example.cardtask.itemTouch.ItHelperInterface
import kotlinx.android.synthetic.main.model_user.view.*

class RvUsersInterface(context: Fragment) : RecyclerView.Adapter<RvUsersInterface.ViewHolder>(), ItHelperInterface {

    private var rvUserList = mutableListOf<UserGroupResponse.UsersData>()
    val context = context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.tv_model_userName
        val userPhoto: ImageView = itemView.img_model_userPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvUsersInterface.ViewHolder {
        val userView = LayoutInflater.from(parent.context).inflate(R.layout.model_user, parent, false)
        return ViewHolder(userView)
    }

    override fun getItemCount(): Int {
        return rvUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = rvUserList[position].username
        if (rvUserList[position].image != "null") {
            Glide.with(context).asBitmap()
                .load("https://storage.googleapis.com/gcs.gill.gq/" + rvUserList[position].image)
                .transform(CircleCrop()).into(holder.userPhoto)
        }
    }

    fun update(updateList: MutableList<UserGroupResponse.UsersData>) {
        rvUserList.clear()
        rvUserList.addAll(updateList)
        notifyDataSetChanged()
    }

    //  上下滑動
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        TODO("Not yet implemented")
    }

    //  左右滑動
    override fun onItemDismiss(position: Int) {
        rvUserList.removeAt(position)

        notifyItemRemoved(position)
    }
}