package com.example.nexus.model

import android.graphics.Bitmap

data class UserData(
    val userName : String,
    val recentMessage : String,
    val image : Bitmap ? = null,
    val recentTime : String,
    val messageCount : Int
)
