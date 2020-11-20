package com.example.cardtask.recyclerView

import android.content.Context
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
import com.example.cardtask.api.CardResponse
import com.example.cardtask.api.UserGroupResponse
import kotlinx.android.synthetic.main.fragment_ed_task.view.*
import kotlinx.android.synthetic.main.model_user.view.*

class RvUsersAdapter(context: Fragment) : RecyclerView.Adapter<RvUsersAdapter.ViewHolder>() {

    private var rvUserList = mutableListOf<UserGroupResponse.UsersData>()
    val context = context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.tv_model_userName
        val userPhoto: ImageView = itemView.img_model_userPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvUsersAdapter.ViewHolder {
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
}