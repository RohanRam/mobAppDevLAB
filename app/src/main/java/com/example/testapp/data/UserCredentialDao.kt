package com.example.testapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserCredentialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredential(credential: UserCredential)

    @Query("SELECT * FROM user_credentials WHERE `key` = :key LIMIT 1")
    fun getCredential(key: String): UserCredential?

    @Insert
    fun insertLoginRecord(record: LoginRecord)

    @Query("SELECT * FROM login_records ORDER BY timestamp DESC")
    fun getAllLoginRecords(): List<LoginRecord>
}