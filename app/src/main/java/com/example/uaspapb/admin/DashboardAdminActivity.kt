package com.example.uaspapb.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspapb.MainActivity
import com.example.uaspapb.databinding.ActivityHomeAdminBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            //Logout
            btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(this@DashboardAdminActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            btnAdd.setOnClickListener {
                val intent = Intent(this@DashboardAdminActivity, UploadAdminActivity::class.java)
                startActivity(intent)
            }
        }
    }
}