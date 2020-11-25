package com.example.cardtask.recyclerView

import android.util.Log
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
import com.example.cardtask.api.*
import com.example.cardtask.fragment.MainFragment.Companion.isUserDeleted
import com.example.cardtask.itemTouch.IthHelperInterface
import kotlinx.android.synthetic.main.model_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RvUsersAdapter(val context: Fragment) : RecyclerView.Adapter<RvUsersAdapter.ViewHolder>(), IthHelperInterface {

    private var rvUserList = mutableListOf<UserGroupResponse.UsersData>()
    var onUserRemove: (MutableList<UserGroupResponse.UsersData>, Int, DelUser) -> Unit =
        { mutableList: MutableList<UserGroupResponse.UsersData>, i: Int, delUser: DelUser -> }

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

    //  上下滑動
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        TODO("Not yet implemented")
    }

    //  左右滑動
    override fun onItemDismiss(position: Int, cardId: Int) {
        val userId = DelUser(rvUserList[position].id)
        onUserRemove.invoke(rvUserList, position, userId)
//        onUserRemove.invoke(rvUserList[position].id,cardId)
//        Log.d("dismiss", "$userId")
//        Api.retrofitService.delUser(token, cardId, userId)
//            .enqueue(object : Callback<DelUserResponse> {
//                override fun onFailure(call: Call<DelUserResponse>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onResponse(call: Call<DelUserResponse>, response: Response<DelUserResponse>) {
//                    if (response.isSuccessful) {
//                        rvUserList.removeAt(position)
//                        notifyItemRemoved(position)
//                        //將companion object isUserDeleted設爲true,讓bottom sheet dialog dismiss listener偵測
//                        isUserDeleted = true
//                    }
//                }
//            })

    }
}