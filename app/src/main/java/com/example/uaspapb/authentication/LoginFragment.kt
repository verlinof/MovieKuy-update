package com.example.uaspapb.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uaspapb.Helper
import com.example.uaspapb.MainActivity
import com.example.uaspapb.R
import com.example.uaspapb.databinding.ActivityHomeAdminBinding
import com.example.uaspapb.databinding.FragmentLoginBinding
import com.example.uaspapb.user.HomeUserActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var helper: Helper
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        val auth = Firebase.auth

        //Helper
        helper = Helper(requireContext())

        with(binding) {
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            btnLogin.setOnClickListener {
                if(checkInputField()) {
                    auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener {
                        task ->
                        if(task.isSuccessful) {
                            val currentUser = auth.currentUser
                            isUser(currentUser)
                        }else {
                            Toast.makeText(requireContext(), "Login Failed, Check your Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else {
                    Toast.makeText(requireContext(), "Check all the input!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun isUser(user: FirebaseUser?) {
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("users").document(user!!.uid)
            .get().addOnSuccessListener {
                    document ->
                if(document != null && document.exists()) {
                    val userData = document.data!!

                    val role = userData["role"] as String
                    if(role == "user") {
                        Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), HomeUserActivity::class.java)
                        startActivity(intent)
                        activity?.finishAffinity()
                    }else {
                        Toast.makeText(requireContext(), "User Account Not Found", Toast.LENGTH_SHORT).show()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}