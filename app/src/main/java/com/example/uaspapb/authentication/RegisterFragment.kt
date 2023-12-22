package com.example.uaspapb.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uaspapb.Helper
import com.example.uaspapb.R
import com.example.uaspapb.databinding.FragmentRegisterBinding
import com.example.uaspapb.user.HomeUserActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var helper: Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        helper = Helper(requireContext())

        with(binding) {
            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if(checkInputField()) {
                    Firebase.auth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val user = Firebase.auth.currentUser
                            setRole(user!!)

                            startActivity(Intent(requireContext(), HomeUserActivity::class.java))
                            activity?.finish()

                        }else {
                            Log.e("Registration", it.exception.toString() )
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun setRole(user: FirebaseUser) {
        val userData = HashMap<String, Any>()
        userData["email"] = binding.etEmail.text.toString()
        userData["username"] = binding.etUsername.text.toString()
        userData["role"] = helper.getStatus().toString()

        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("users").document(user.uid)
            .set(userData)
            .addOnCompleteListener {
                helper.setUsername(binding.etUsername.text.toString())
                Toast.makeText(requireContext(), "Account has been created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error : $e", Toast.LENGTH_SHORT).show()
            }

    }

    private fun checkInputField(): Boolean {
        with(binding) {
            val email = etEmail.text.toString()
            if(etEmail.text.toString() == "") {
                etEmail.error = "This Field is Required"
                return false
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Check Email Format"
            }
            if(etUsername.text.toString() == "") {
                etUsername.error = "This Field is Required"
                return false
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}