package com.example.uaspapb.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uaspapb.MainActivity
import com.example.uaspapb.R
import com.example.uaspapb.databinding.FragmentDashboardUserBinding
import com.example.uaspapb.databinding.FragmentProfileUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragmentUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragmentUser : Fragment() {
    private lateinit var binding: FragmentProfileUserBinding
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileUserBinding.inflate(layoutInflater)

        with(binding) {
            //Get User Credentials
            tvEmail.text = currentUser?.email

            firestore.collection("users").document(currentUser!!.uid)
                .get().addOnSuccessListener {
                        document ->
                    if(document != null && document.exists()) {
                        val data = document.data!!
                        val username = data["username"] as String
                        tvUsername.text = username
                    }
                }.addOnFailureListener {
                    val username = "username"
                    tvUsername.text = username

                    Toast.makeText(requireContext(), "Error : $it", Toast.LENGTH_SHORT).show()
                }

            btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finishAffinity()
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragmentUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}