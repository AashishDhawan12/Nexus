package com.example.nexus.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nexus.R
import com.example.nexus.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.io.encoding.Base64

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
                }
            }
        }
    }
}