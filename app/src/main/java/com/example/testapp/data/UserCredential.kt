package com.example.testapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
data class UserCredential(
    @PrimaryKey
    val key: String,
    val value: String
)