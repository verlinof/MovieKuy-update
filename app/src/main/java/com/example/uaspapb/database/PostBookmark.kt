package com.example.uaspapb.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("posts_bookmark")
data class PostBookmark(
    @PrimaryKey(true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo("post_id")
    @NonNull
    val postId: String,

    @ColumnInfo("email")
    @NonNull
    val email: String
)
