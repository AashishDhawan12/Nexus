package com.example.nexus.activity

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

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //binding.chatRecyclerView.adapter = HomeAdapter(list)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}