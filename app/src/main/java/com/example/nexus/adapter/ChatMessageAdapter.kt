package com.example.nexus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.databinding.ReceiverChatBinding
import com.example.nexus.databinding.SenderChatBinding
import com.example.nexus.model.Message
import com.google.firebase.auth.FirebaseAuth

class ChatMessageAdapter(val messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val SENDER_VIEW_TYPE = 1
    val RECEIVER_VIEW_TYPE = 2


    inner class senderViewHolder(val senderView: SenderChatBinding) : RecyclerView.ViewHolder(senderView.root)
    inner class receiverViewHolder(val receiverView: ReceiverChatBinding) : RecyclerView.ViewHolder(receiverView.root)

    override fun getItemViewType(position: Int): Int {
        if(FirebaseAuth.getInstance().currentUser!!.uid.equals(messageList[position].senderId)){
            return SENDER_VIEW_TYPE
        }else{
            return RECEIVER_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SENDER_VIEW_TYPE){
            return senderViewHolder(SenderChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return receiverViewHolder(ReceiverChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if(holder.javaClass == senderViewHolder::class.java){
            val vh = holder as senderViewHolder
            vh.senderView.txtSenderMessage.text = message.message
            vh.senderView.txtSenderTime.text = message.timestamp
        }else{
            val vh = holder as receiverViewHolder
            vh.receiverView.txtReceiverMessage.text = message.message
            vh.receiverView.txtReceiverTime.text = message.timestamp
        }
    }
}