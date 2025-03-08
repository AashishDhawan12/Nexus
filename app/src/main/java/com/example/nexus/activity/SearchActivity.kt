package com.example.nexus.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nexus.adapter.SearchUserAdapter
import com.example.nexus.databinding.ActivitySearchBinding
import com.example.nexus.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchUserAdapter
    private lateinit var db: DatabaseReference
     var userList= mutableListOf<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = SearchUserAdapter(userList)

        binding.btnSearch.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            db = FirebaseDatabase.getInstance().getReference("users")
            val query = binding.etSearchUser.text.toString().lowercase()
            var q:Query = db.orderByKey()
            q = db.orderByChild("userName").startAt(query).endAt(query + "\uf8ff")

            q.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        userList.clear()

                        for(snap in snapshot.children){
                            val user = snap.getValue(UserData::class.java)
                            if(user != null){
                                userList.add(user)
                            }
                        }

                        binding.searchList.adapter?.notifyDataSetChanged()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                }

            })
        }
        binding.searchList.layoutManager = LinearLayoutManager(this)
        binding.searchList.adapter = adapter


    }
}