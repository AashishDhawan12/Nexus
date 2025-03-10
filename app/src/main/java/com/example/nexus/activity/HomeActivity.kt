package com.example.nexus.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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
    var list = mutableListOf<UserData>()
    var contactList = mutableListOf<String>()
    private lateinit var adapter: HomeAdapter
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        adapter = HomeAdapter(list)
        reference = FirebaseDatabase.getInstance().getReference()

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnChats.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        binding.btnMenu.setOnClickListener {
            val pop = PopupMenu(this,binding.btnMenu)
            pop.inflate(R.menu.options_menu)

            pop.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuProfile -> {
                        true
                    }
                    R.id.menuLogout -> {
                        auth.signOut()
                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", false)
                        editor.putString("userId", null)
                        editor.apply()

                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            pop.show()
        }

        binding.chatRecyclerView.visibility = View.GONE
        binding.noChat.visibility = View.VISIBLE

        fetchUser()

        val user = auth.currentUser
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(user != null) {
            editor.putBoolean("isLoggedIn", true)
            editor.putString("userId", user.uid)
            editor.apply()
        }

    }
    fun fetchContactDetails() {
        list.clear() // Clear the list before fetching new data.
        for (uid in contactList) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(UserData::class.java)
                    if (userData != null) {
                        list.add(userData)
                        adapter.notifyDataSetChanged() // Notify adapter here!
                        Toast.makeText(this@HomeActivity, "${userData.userName}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    private fun fetchUser() {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(auth.currentUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserData::class.java)
                    val temp = userData?.contacts
                    contactList.clear()
                    for (i in temp!!.values) {
                        contactList.add(i)
                    }
                    if (contactList.isNotEmpty()) {
                        binding.chatRecyclerView.visibility = View.VISIBLE
                        binding.noChat.visibility = View.GONE
                        fetchContactDetails()
                    }else{
                        binding.chatRecyclerView.visibility = View.GONE
                        binding.noChat.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}