package com.example.nexus.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexus.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val db = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if(userID != null){
            db.child(userID).get().addOnSuccessListener {
                snapshot -> if(snapshot.exists()){
                    binding.userName.text = snapshot.child("userName").value.toString()
                    binding.userEmail.text = snapshot.child("email").value.toString()

                    val img = snapshot.child("image").value.toString() // uri
                if(img.isEmpty()){
                    return@addOnSuccessListener
                }
                val decodedBytes = android.util.Base64.decode(img, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                binding.userImg.setImageBitmap(bitmap)
                }
            }
        }
    }
}