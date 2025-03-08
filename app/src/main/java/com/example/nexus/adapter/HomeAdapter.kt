package com.example.nexus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.databinding.SingleChatItemBinding
import com.example.nexus.model.UserData

class HomeAdapter (private val list: MutableList<UserData>): RecyclerView.Adapter<HomeAdapter.ViewModel>() {
    inner class ViewModel (val binding: SingleChatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val view = SingleChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewModel(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
//        holder.binding.imgUser.setImageBitmap(list[position].image)
        holder.binding.txtUserName.text = list[position].userName
//        holder.binding.txtRecentMessage.text = list[position].recentMessage
//        holder.binding.txtTime.text = list[position].recentTime
//        holder.binding.txtMessageCount.text = list[position].messageCount.toString()
    }

}

