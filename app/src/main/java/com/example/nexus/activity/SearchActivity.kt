package com.example.nexus.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nexus.adapter.SearchUserAdapter
import com.example.nexus.databinding.ActivitySearchBinding
import com.example.nexus.model.UserData
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
        adapter = SearchUserAdapter(userList,this)

        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    findUser(query)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null) {
                    findUser(newText)
                }
                return true
            }

        })


        binding.searchList.layoutManager = LinearLayoutManager(this)
        binding.searchList.adapter = adapter
    }

    private fun findUser(query: String) {
        binding.progressBar.visibility = View.VISIBLE

        if(query.isEmpty()){
            userList.clear()
            adapter.notifyDataSetChanged()
            binding.progressBar.visibility = View.GONE
            return
        }

        db = FirebaseDatabase.getInstance().getReference("users")
        var q:Query = db.orderByChild("userName").startAt(query.toLowerCase(Locale.ROOT)).endAt(query + "\uf8ff")

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
}