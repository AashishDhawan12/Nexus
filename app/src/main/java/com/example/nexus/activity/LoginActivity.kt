package com.example.nexus.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.nexus.R
import com.example.nexus.databinding.ActivityLoginBinding
import com.example.nexus.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient;
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignupWithGoogle.setOnClickListener{
            signinGoogle()
        }

        binding.btnLogin.setOnClickListener {
            val email  = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if(email == "" && password == ""){
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        Toast.makeText(this,"Logged in successfully",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                          Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signinGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            handleResults(task)
        }else{
            Toast.makeText(this,"something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            if(account!=null){
                val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credentials).addOnCompleteListener(this){ authTask ->
                    if(authTask.isSuccessful){
                        val uid = auth.currentUser!!.uid
                        val user = UserData(userName = account.displayName,email = account.email, uid = uid)
                        database.child("users").child(auth.currentUser!!.uid).setValue(user)
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        // Log Firebase Auth error
                        Log.e("LoginActivity", "Firebase Auth with Google failed: ${authTask.exception}")
                        Toast.makeText(this, "Firebase authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Google account is null", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Google account is null after sign-in")
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            // Log Google Sign-In API error
            Log.e("LoginActivity", "Google Sign-In failed: ${e.statusCode}", e)
            Toast.makeText(this, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Log any other unexpected errors
            Log.e("LoginActivity", "An unexpected error occurred: ${e.message}", e)
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
    }

}