package com.example.uaspapb.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.uaspapb.R
import com.example.uaspapb.databinding.ActivityHomeUserBinding

class HomeUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            navbarUser.selectedItemId = R.id.itemDashboard
            replaceFragment(DashboardFragmentUser())

            navbarUser.setOnItemSelectedListener() {
                when(it.itemId) {
                    R.id.itemDashboard -> replaceFragment(DashboardFragmentUser())
                    R.id.itemBookmark -> replaceFragment(DashboardFragmentUser())
                    R.id.itemProfile -> replaceFragment(DashboardFragmentUser())
                    else -> {}
                }
                true
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)

        fragmentTransaction.commit()
    }
}