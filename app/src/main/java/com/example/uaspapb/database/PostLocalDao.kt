package com.example.uaspapb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uaspapb.model.Post

@Dao
interface PostLocalDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insert(post: PostLocal)

    @Update
    fun update(post: PostLocal)

    @Query("DELETE FROM posts_local")
    fun truncateTable()

    @Query("DELETE FROM posts_local WHERE id = :postId")
    fun deleteById(postId: String)

    @Query("SELECT * FROM posts_local")
    fun allPostsLocal(): LiveData<List<PostLocal>>
}