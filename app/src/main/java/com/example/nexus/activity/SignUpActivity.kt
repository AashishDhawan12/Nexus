package com.example.nexus.activity

import android.content.Intent
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
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri ->
                if(uri != null){
                    binding.imgAppLogo.setImageURI(uri)
                    imageUri = uri
                }
        }

        database = FirebaseDatabase.getInstance().getReference()

        auth = FirebaseAuth.getInstance()

        binding.floatingActionButton.setOnClickListener{
            pickImageLauncher.launch("image/*")
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
                        uploadImageAndStoreUrl(auth.currentUser!!.uid,imageUri)
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    fun uploadImageAndStoreUrl(userId: String, imageUri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("images/$userId/profile.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val database = Firebase.database
                    val userRef = database.reference.child("users/$userId/image")
                    userRef.setValue(downloadUrl.toString())
                    println("Image uploaded and URL stored: $downloadUrl")
                }
            }
            .addOnFailureListener { exception ->
                println("Error uploading image or storing URL: $exception")
            }
    }
}