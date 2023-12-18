package com.example.uaspapb.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("posts_local")
data class PostLocal(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: String,

    @ColumnInfo("post_description")
    @NonNull
    val postDescription: String,

    @ColumnInfo("post_image")
    @NonNull
    val postImage: String,

    @ColumnInfo("post_title")
    @NonNull
    val postTitle: String
)
