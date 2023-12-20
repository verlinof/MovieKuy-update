package com.example.uaspapb.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.uaspapb.databinding.ActivityDetailAdminBinding
import com.example.uaspapb.model.Post
import com.google.firebase.firestore.FirebaseFirestore

class DetailAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var post: Post? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Call Function
        fetchData()

        //Loading Progress
        binding.loadingBar.visibility = View.VISIBLE

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this@DetailAdminActivity, DashboardAdminActivity::class.java))
            finishAffinity()
        }
    }

    private fun fetchData() {
        val bundle: Bundle? = intent.extras
        val id = bundle?.getString("EXTID")

        if (id != null) {
            firestore.collection("posts").document(id)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    if(documentSnapshot.exists()) {
                        val data = documentSnapshot.data
                        post = Post(
                            data!!["id"].toString(),
                            data!!["postDescription"].toString(),
                            data!!["postImage"].toString(),
                            data!!["postTitle"].toString()
                        )

                        //Set Value
                        Glide.with(binding.ivPostImage)
                            .load(post!!.postImage)
                            .centerCrop()
                            .into(binding.ivPostImage)
                        binding.tvTitleData.text = post!!.postTitle
                        binding.tvDescription.text = post!!.postDescription

                        //Remove loading progress
                        binding.loadingBar.visibility = View.INVISIBLE
                    }
                }
        }
    }
}