package com.example.uaspapb

import android.content.Context
import android.content.SharedPreferences

class Helper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun setStatus(status: String) {
        val editor = sharedPreferences.edit()
        editor.putString("status", status)
        editor.apply()
    }

    fun getStatus(): String? {
        val status = sharedPreferences.getString("status", null)
        return status
    }
}