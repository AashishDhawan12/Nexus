package com.example.nexus.model

data class UserData(
    val userName : String? ="",
    val email : String? ="",
    val password : String? ="",
    val image : String? ="",
    val uid : String? = "",
    val contacts: List<String> ? = null
)
