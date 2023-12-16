package com.example.uaspapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uaspapb.admin.DashboardAdminActivity
import com.example.uaspapb.databinding.ActivitySplashScreenBinding
import com.example.uaspapb.user.HomeUserActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            ivLogo.alpha = 0f
            ivLogo.animate().setDuration(1500).alpha(1f).withEndAction() {
                val firestore = FirebaseFirestore.getInstance()
                val auth = Firebase.auth
                val currentUser = auth.currentUser

                if(currentUser != null) {
                    firestore.collection("users").document(currentUser!!.uid)
                        .get().addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val role = document["role"] as String

                                if (role == "admin") {
                                    val intent =
                                        Intent(this@SplashScreen, DashboardAdminActivity::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                } else {
                                    val intent = Intent(this@SplashScreen, HomeUserActivity::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            }
                        }
                }else {
                    val intent = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }
}