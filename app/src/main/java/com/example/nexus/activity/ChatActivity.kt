package com.example.nexus.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nexus.adapter.ChatMessageAdapter
import com.example.nexus.databinding.ActivityChatBinding
import com.example.nexus.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private var messageList = mutableListOf<Message>()
    private lateinit var adapter: ChatMessageAdapter

    private lateinit var binding : ActivityChatBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val senderuid = FirebaseAuth.getInstance().currentUser!!.uid
        val receiverid = intent.getStringExtra("uid")
        val username = intent.getStringExtra("name")

        senderRoom = senderuid + receiverid
        receiverRoom = receiverid + senderuid
        adapter = ChatMessageAdapter(messageList)

        binding.txtUser.text = username

        fetchChat()

        binding.messageRecycler.layoutManager = LinearLayoutManager(this)
        binding.messageRecycler.adapter = adapter

        binding.btnSendMessage.setOnClickListener {
            val message = binding.edMessage.text.toString()
            if(message.isEmpty()){
                return@setOnClickListener
            }
            binding.edMessage.setText("")
            val now = LocalDateTime.now()
            val time = now.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
            val messageObject = Message(message,senderuid,time)
            sendMessage(messageObject)
            addContact(username,senderuid,receiverid)
        }

    }

    private fun addContact(username: String?, senderuid: String?, receiverid: String?) {
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("users").child(senderuid!!).child("contacts")
            .setValue(hashMapOf(Pair(username,receiverid))).addOnCompleteListener{
                dbRef.child("users").child(receiverid!!).child("contacts")
                    .setValue(hashMapOf(Pair(username,senderuid)))
            }
    }

    private fun fetchChat() {
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    messageList.clear()
                    for (snap in snapshot.children){
                        val message = snap.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendMessage(messageObject: Message) {
        val dbRef = FirebaseDatabase.getInstance().getReference()

        dbRef.child("chats").child(senderRoom!!).push().setValue(messageObject).addOnSuccessListener {
            dbRef.child("chats").child(receiverRoom!!).push().setValue(messageObject)
        }
    }
}