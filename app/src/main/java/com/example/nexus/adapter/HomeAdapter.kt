package com.example.nexus.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.collection.intIntMapOf
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.activity.ChatActivity
import com.example.nexus.databinding.SingleChatItemBinding
import com.example.nexus.model.UserData

class HomeAdapter (val context: Context, private val data: MutableList<UserData>): RecyclerView.Adapter<HomeAdapter.ViewModel>() {
    inner class ViewModel (val binding: SingleChatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val view = SingleChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewModel(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val curr = data[position]
        val decodedBytes = Base64.decode(curr.image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        holder.binding.imgUser.setImageBitmap(bitmap)
        holder.binding.txtUserName.text = curr.userName

        holder.binding.profileContainer.setOnClickListener {
            val int = Intent(context, ChatActivity::class.java)
            int.putExtra("name",curr.userName)
            int.putExtra("uid",curr.uid)
            context.startActivity(int)
        }
    }

}

