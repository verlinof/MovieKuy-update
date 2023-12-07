package com.example.uaspapb.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspapb.R
import com.example.uaspapb.databinding.ActivityAuthenticationBinding
import com.google.android.material.tabs.TabLayoutMediator

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            viewPager.adapter = AuthenticationPagerAdapter(this@AuthenticationActivity)
            TabLayoutMediator(tabLayout, viewPager) {
                tab, position ->
                tab.text = when(position) {
                    0 -> "Login"
                    1 -> "Register"
                    else -> ""
                }
            }.attach()
        }
    }


}