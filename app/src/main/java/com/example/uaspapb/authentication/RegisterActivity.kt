package com.example.uaspapb.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspapb.Helper
import com.example.uaspapb.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var status: String
    private lateinit var helper: Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }

            helper = Helper(this@RegisterActivity)

            //Check apakah milih Admin/User
            val loginStatus = helper.getStatus()
            if(loginStatus == "admin") {
                tvTitle.text = "Register Admin"
            }else{
                tvTitle.text = "Register User"
            }

            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if(checkInputField()) {
                    Firebase.auth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val user = it.result.user
                            setRole(user!!)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()

                        }else {
                            Log.e("Registration", it.exception.toString() )
                        }
                    }
                }
            }
        }
    }

    private fun setRole(user: FirebaseUser) {
        val userData = HashMap<String, Any>()
        userData["email"] = binding.etEmail.text.toString()
        userData["role"] = helper.getStatus().toString()

        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("users").document(user.uid)
            .set(userData)
            .addOnCompleteListener {
                Toast.makeText(this@RegisterActivity, "Account has been created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@RegisterActivity, "Error : $e", Toast.LENGTH_SHORT).show()
            }

    }

    private fun checkInputField(): Boolean {
        with(binding) {
            val email = etEmail.text.toString()
            if(etEmail.text.toString() == "") {
                etEmail.error = "This Field is Required"
                return false
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Check Email Format"
            }
            if(etPassword.text.toString() == "") {
                etPassword.error = "This Field is Required"
                return false
            }
            if(etRePassword.text.toString() == "") {
                etRePassword.error = "This Field is Required"
                return false
            }
            if(etPassword.text.toString() != etRePassword.text.toString()) {
                etPassword.error = "Password and Re-password need to be same"
                etRePassword.error = "Password and Re-password need to be same"

                return false
            }

            return true
        }
    }

}