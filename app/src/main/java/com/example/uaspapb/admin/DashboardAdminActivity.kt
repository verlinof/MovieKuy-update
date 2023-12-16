package com.example.uaspapb.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.MainActivity
import com.example.uaspapb.databinding.ActivityHomeAdminBinding
import com.example.uaspapb.model.Post
import com.example.uaspapb.user.PostAdapterAdmin
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val firestore = FirebaseFirestore.getInstance()
    private var postList: ArrayList<Post> = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            //Function Calling
            getUserCredential()
            fetchData()

            //Logout
            btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(this@DashboardAdminActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            //Upload Content
            btnAdd.setOnClickListener {
                val intent = Intent(this@DashboardAdminActivity, UploadAdminActivity::class.java)
                startActivity(intent)
            }

            //RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this@DashboardAdminActivity)
            recyclerView.setHasFixedSize(true)
            val adapter = PostAdapterAdmin(postList)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object: PostAdapterAdmin.onItemClickListener {
                override fun onItemClick(position: Int) {
                }

                override fun onEditClick(position: Int) {
                    val intent = Intent(this@DashboardAdminActivity, EditAdminActivity::class.java)
                    intent.putExtra("EXTID", postList[position].id)
                    startActivity(intent)
                }

                override fun onDeleteClick(position: Int) {
                }

            })
        }
    }

    //Function
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

                Toast.makeText(this@DashboardAdminActivity, "Error : $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        //Clear previous data
        postList.clear()

        firestore.collection("posts").get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
            }
            .addOnFailureListener {exception ->
                Toast.makeText(this@DashboardAdminActivity, "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }
}