package com.example.nexus.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
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
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private  var bitmap : Bitmap? = null
    private var senderuid: String?= null;
    private var messageList = mutableListOf<Message>()
    private lateinit var adapter: ChatMessageAdapter

    private lateinit var binding : ActivityChatBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        senderuid = FirebaseAuth.getInstance().currentUser!!.uid
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
            val messageObject = Message(senderId = senderuid,message = message,timestamp = time)
            sendMessage(messageObject)
            addContact(username,senderuid,receiverid)
        }

        binding.btnSendImage.setOnClickListener{
            selectImage()
        }

    }

    private fun addContact(username: String?, senderuid: String?, receiverid: String?) {
        //setting contact for sender
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("users").child(senderuid!!).child("contacts").get().addOnSuccessListener {
            if(it.exists()){
                val list = it.getValue() as MutableList<String>
                if(!list.contains(receiverid)){
                    list.add(receiverid!!)
                }
                dbRef.child("users").child(senderuid).child("contacts").setValue(list)
            }else{
                dbRef.child("users").child(senderuid).child("contacts").setValue(listOf(receiverid))
            }

            // set contact for Receiver

            dbRef.child("users").child(receiverid!!).child("contacts").get().addOnSuccessListener {
                if(it.exists()){
                    val list = it.getValue() as MutableList<String>
                    if(!list.contains(senderuid)){
                        list.add(senderuid)
                    }
                    dbRef.child("users").child(receiverid).child("contacts").setValue(list)
                }else{
                    dbRef.child("users").child(receiverid).child("contacts").setValue(listOf(senderuid))
                }
            }

        }

    }

    private fun fetchChat() {
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("chats").child(senderRoom!!)
            .addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    messageList.clear()
                    for (snap in snapshot.children){
                        val message = snap.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    adapter.notifyDataSetChanged()
                    binding.messageRecycler.scrollToPosition(messageList.size-1)
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

    private fun selectImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,109)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                bitmap = selectedImage
                val encodedImage = bitmapToBase64(bitmap!!)
                val now = LocalDateTime.now()
                val time = now.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))

                sendMessage(Message(senderuid,pic = encodedImage,timestamp = time))

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }
}