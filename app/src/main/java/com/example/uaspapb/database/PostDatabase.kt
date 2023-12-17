package com.example.uaspapb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [PostRoom::class], version = 1, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao?
    companion object{
        @Volatile
        private var INSTANCE: PostDatabase? = null
        fun getDatabase(context: Context): PostDatabase? {
            if(INSTANCE == null){
                synchronized(PostDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        PostDatabase::class.java, "note_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}