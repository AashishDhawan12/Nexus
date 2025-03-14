package com.example.nexus.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.databinding.SingleRowUsersProfileBinding
import com.example.nexus.model.UserData
import android.util.Base64
import com.example.nexus.activity.ChatActivity

class SearchUserAdapter(private val userList: MutableList<UserData>, val context: Context) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: SingleRowUsersProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SingleRowUsersProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.binding.txtSearchUser.text = currentUser.userName

        val decodedBytes = Base64.decode(currentUser.image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.binding.imgSearchUser.setImageBitmap(bitmap)

        holder.binding.singleItemLayout.setOnClickListener{
            val int = Intent(context, ChatActivity::class.java)
            int.putExtra("name",currentUser.userName)
//            int.putExtra("image",currentUser.image)
            int.putExtra("uid",currentUser.uid)
            context.startActivity(int)
        }
    }
}