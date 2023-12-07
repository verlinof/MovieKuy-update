package com.example.uaspapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspapb.authentication.AuthenticationActivity
import com.example.uaspapb.authentication.LoginActivity
import com.example.uaspapb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnAdmin.setOnClickListener {
                val helper = Helper(this@MainActivity)
                helper.setStatus("admin")

                startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            }
            btnUser.setOnClickListener {
                val helper = Helper(this@MainActivity)
                helper.setStatus("user")

                startActivity(Intent(this@MainActivity,AuthenticationActivity::class.java))
            }
        }
    }
}