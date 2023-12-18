package com.example.uaspapb

import android.content.Context
import android.content.SharedPreferences
import android.provider.ContactsContract.CommonDataKinds.Email

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

    fun setUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    fun getUsername(): String? {
        val username = sharedPreferences.getString("username", "username")
        return username
    }

    fun setEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", email)
        editor.apply()
    }

    fun getEmail(): String? {
        val username = sharedPreferences.getString("username", "username")
        return username
    }

}