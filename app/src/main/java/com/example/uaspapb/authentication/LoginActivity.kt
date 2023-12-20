package com.example.uaspapb.authentication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.uaspapb.Helper
import com.example.uaspapb.MainActivity
import com.example.uaspapb.R
import com.example.uaspapb.admin.DashboardAdminActivity
import com.example.uaspapb.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var helper: Helper
    private lateinit var email: String
    private lateinit var password: String
    //Notification
    private val channelId = "Login_notification"
    private val notifId = 90
    private lateinit var notifManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth

        //Notification
        notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Helper
        helper = Helper(this@LoginActivity)

        with(binding) {
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            btnLogin.setOnClickListener {
                if(checkInputField()) {
                    auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener(this@LoginActivity) {
                        val currentUser = auth.currentUser
                        isAdmin(currentUser)
                    }.addOnFailureListener {
                        Toast.makeText(this@LoginActivity, "Error : $it", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this@LoginActivity, "Check all the input!", Toast.LENGTH_SHORT).show()
                }
            }
            btnBack.setOnClickListener {
                finish()
            }
        }

    }

    private fun isAdmin(user: FirebaseUser?) {
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("users").document(user!!.uid)
            .get()
            .addOnSuccessListener {
                document ->
                if(document != null && document.exists()) {
                    val userData = document.data!!

                    val role = userData["role"] as String
                    val username = userData["username"] as String
                    if(role == "admin") {
                        //SharedPreferences
                        helper.setUsername(username)

                        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, DashboardAdminActivity::class.java)
                        startActivity(intent)
                        finishAffinity()

                        //Notification
                        showNotification()
                    }else {
                        Toast.makeText(this@LoginActivity, "Admin Account Not Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity, "Error : $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkInputField():Boolean {
        with(binding) {
            if(etEmail.text.toString() == "") {
                etEmail.error = "This Field is Required"
                return false
            }
            if(etPassword.text.toString() == "") {
                etPassword.error = "This Field is Required"
                return false
            }
        }
        return true
    }

    private fun showNotification() {
        val currentUser = Firebase.auth.currentUser
        val email = currentUser?.email

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Login Status")
            .setSmallIcon(R.drawable.ic_notification_24)
            .setContentText("Welcome back, $email")
            .setAutoCancel(true)

        val notifChannel = NotificationChannel(
            channelId,
            "Notifku",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        with(notifManager) {
            createNotificationChannel(notifChannel)
            notify(0, builder.build())
        }
    }
}