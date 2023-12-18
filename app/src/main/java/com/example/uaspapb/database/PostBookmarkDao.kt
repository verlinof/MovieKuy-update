package com.example.uaspapb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostBookmarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(post: PostBookmark)

    @Update
    fun update(post: PostBookmark)

    @Delete
    fun delete(post: PostBookmark)

    @Query("DELETE FROM posts_bookmark WHERE post_id = :postId")
    fun deleteById(postId: String)

    @Query("Select * from posts_bookmark WHERE email = :emailData")
    fun allPostsByUsername(emailData: String): LiveData<List<PostBookmark>>
}