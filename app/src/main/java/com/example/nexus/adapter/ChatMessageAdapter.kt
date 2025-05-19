package com.example.nexus.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nexus.databinding.ReceiverChatBinding
import com.example.nexus.databinding.ReceiverImageBinding
import com.example.nexus.databinding.SenderChatBinding
import com.example.nexus.databinding.SenderImageBinding
import com.example.nexus.model.Message
import com.google.firebase.auth.FirebaseAuth

class ChatMessageAdapter(val messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val SENDER_VIEW_TYPE = 1
    val RECEIVER_VIEW_TYPE = 2
    val SENDER_IMAGE_VIEW_TYPE = 3
    val RECEIVER_IMAGE_VIEW_TYPE = 4


    inner class senderViewHolder(val senderView: SenderChatBinding) : RecyclerView.ViewHolder(senderView.root)
    inner class senderImageViewHolder(val senderImageView: SenderImageBinding) : RecyclerView.ViewHolder(senderImageView.root)
    inner class receiverViewHolder(val receiverView: ReceiverChatBinding) : RecyclerView.ViewHolder(receiverView.root)
    inner class receiverImageViewHolder(val receiverImageView: ReceiverImageBinding) : RecyclerView.ViewHolder(receiverImageView.root)

    override fun getItemViewType(position: Int): Int {
        if(FirebaseAuth.getInstance().currentUser!!.uid.equals(messageList[position].senderId)){
            if(messageList[position].message!!.isEmpty()){
                return SENDER_IMAGE_VIEW_TYPE
            }
            return SENDER_VIEW_TYPE
        }else{
            if(messageList[position].message!!.isEmpty()){
                return RECEIVER_IMAGE_VIEW_TYPE
            }
            return RECEIVER_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SENDER_VIEW_TYPE){
            return senderViewHolder(SenderChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else if(viewType == SENDER_IMAGE_VIEW_TYPE){
            return senderImageViewHolder(SenderImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else if(viewType == RECEIVER_IMAGE_VIEW_TYPE){
            return receiverImageViewHolder(ReceiverImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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
        }else if(holder.javaClass == senderImageViewHolder::class.java){
            val vh = holder as senderImageViewHolder
            val decodedBytes = Base64.decode(message.pic, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            vh.senderImageView.ImgSender.setImageBitmap(bitmap)
            vh.senderImageView.txtSenderTime.text = message.timestamp
        }
        else if(holder.javaClass == receiverImageViewHolder::class.java){
            val vh = holder as receiverImageViewHolder
            val decodedBytes = Base64.decode(message.pic, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            vh.receiverImageView.ImgReceiver.setImageBitmap(bitmap)
            vh.receiverImageView.txtReceiverTime.text = message.timestamp
        }
        else{
            val vh = holder as receiverViewHolder
            vh.receiverView.txtReceiverMessage.text = message.message
            vh.receiverView.txtReceiverTime.text = message.timestamp
        }
    }
}