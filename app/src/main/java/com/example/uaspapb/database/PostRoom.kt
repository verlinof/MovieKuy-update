package com.example.uaspapb.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.checkerframework.common.aliasing.qual.Unique

@Entity("posts")
data class PostRoom(
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
