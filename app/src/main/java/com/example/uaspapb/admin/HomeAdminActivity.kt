package com.example.uaspapb.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspapb.R
import com.example.uaspapb.databinding.ActivityHomeAdminBinding

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}