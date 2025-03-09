package com.example.nexus.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nexus.R
import com.example.nexus.adapter.HomeAdapter
import com.example.nexus.databinding.ActivityHomeBinding
import com.example.nexus.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var reference: DatabaseReference
    var list= mutableListOf<UserData>()
    private lateinit var adapter: HomeAdapter
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        adapter = HomeAdapter(list)

        reference = FirebaseDatabase.getInstance().getReference()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChats.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }


        reference = FirebaseDatabase.getInstance().getReference()
        reference.child("users").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for(data in snapshot.children){
                        val user = data.getValue(UserData::class.java)
                        if(user != null){
                            list.add(user)
                        }
                    }
                    binding.chatRecyclerView.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )

        binding.chatRecyclerView.adapter = HomeAdapter(list)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val user = auth.currentUser
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(user != null) {
            editor.putBoolean("isLoggedIn", true)
            editor.putString("userId", user.uid)
            editor.apply()
        }

    }
}