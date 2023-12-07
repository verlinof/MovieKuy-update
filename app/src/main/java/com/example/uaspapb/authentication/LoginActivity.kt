package com.example.uaspapb.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.uaspapb.Helper
import com.example.uaspapb.admin.HomeAdminActivity
import com.example.uaspapb.databinding.ActivityLoginBinding
import com.example.uaspapb.user.HomeUserActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var helper: Helper
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth

        //Helper
        helper = Helper(this@LoginActivity)

        with(binding) {
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            btnLogin.setOnClickListener {
                if(checkInputField()) {
                    auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener(this@LoginActivity) {
                        //Job Status
                        val status = helper.getStatus()

                        val currentUser = auth.currentUser
                        isAdmin(currentUser)
                    }
                }else {
                    Toast.makeText(this@LoginActivity, "Check all the input!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isAdmin(user: FirebaseUser?) {
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("users").document(user!!.uid)
            .get().addOnSuccessListener {
                document ->
                if(document != null && document.exists()) {
                    val userData = document.data!!

                    val role = userData["role"] as String
                    if(role == "admin") {
                        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeAdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        Toast.makeText(this@LoginActivity, "Admin Account Not Found", Toast.LENGTH_SHORT).show()
                    }
                }
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
}