package com.example.uaspapb.database

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uaspapb.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(post: PostRoom)

    @Update
    fun update(post: PostRoom)

    @Delete
    fun delete(post: PostRoom)

    @Query("Select * from posts WHERE email = :emailData")
    fun allPostsByUsername(emailData: String): LiveData<List<PostRoom>>
}