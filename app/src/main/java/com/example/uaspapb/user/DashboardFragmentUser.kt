package com.example.uaspapb.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.R
import com.example.uaspapb.databinding.FragmentDashboardUserBinding
import com.example.uaspapb.databinding.FragmentRegisterBinding
import com.example.uaspapb.model.Post
import com.example.uaspapb.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class DashboardFragmentUser : Fragment() {
    private lateinit var binding: FragmentDashboardUserBinding
    private var postList: ArrayList<Post> = ArrayList<Post>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Get User Detail
        getUserCredential()

        binding = FragmentDashboardUserBinding.inflate(inflater)
        with(binding) {
            //Firebase
            fetchData()

            //RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            //Adapter RecyclerView
            val adapter = DashboardPostAdapter(postList)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : DashboardPostAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                }

                override fun onBookmarkClick(position: Int) {
                }

            })

        }
        //Binding
        return binding.root
    }

    private fun getUserCredential() {
        //Get User Credentials
        firestore.collection("users").document(currentUser!!.uid)
            .get().addOnSuccessListener {
                    document ->
                if(document != null && document.exists()) {
                    val data = document.data!!
                    val username = data["username"] as String
                    binding.tvUsername.text = "Hello $username"
                }
            }.addOnFailureListener {
                val username = "username"
                binding.tvUsername.text = "Hello $username"

                Toast.makeText(requireContext(), "Error : $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        firestore.collection("posts").get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
            }
            .addOnFailureListener {exception ->
                Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragmentUser().apply {
                arguments = Bundle().apply {
                }
            }
    }
}