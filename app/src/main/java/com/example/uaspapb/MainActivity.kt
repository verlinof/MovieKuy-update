package com.example.uaspapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.uaspapb.admin.DashboardAdminActivity
import com.example.uaspapb.authentication.AuthenticationActivity
import com.example.uaspapb.authentication.LoginActivity
import com.example.uaspapb.databinding.ActivityMainBinding
import com.example.uaspapb.user.HomeUserActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestore = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        with(binding) {
            //Check User has been login or not
            if(currentUser != null) {
                firestore.collection("users").document(currentUser!!.uid)
                    .get().addOnSuccessListener {
                            document ->
                        if(document != null && document.exists()) {
                            val role = document["role"] as String

                            if(role == "admin") {
                                val intent = Intent(this@MainActivity, DashboardAdminActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }else {
                                val intent = Intent(this@MainActivity, HomeUserActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                        }
                    }
            }else {
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
}