package com.example.nexus.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.nexus.databinding.ActivitySignUpBinding
import com.example.nexus.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.storage

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private  var Bitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        binding.floatingActionButton.setOnClickListener{
            selectImage()
        }

        binding.btnSignup.setOnClickListener {
            val profileName = binding.etProfile.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(profileName == "" && email == "" && password == ""){
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
                    if(task.isSuccessful){
                        val user = UserData(profileName,email,password,"")
                        database.child("users").child(auth.currentUser!!.uid).setValue(user)

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun selectImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,109)
    }




}