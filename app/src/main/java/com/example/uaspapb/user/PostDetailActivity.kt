package com.example.uaspapb.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.uaspapb.R
import com.example.uaspapb.databinding.ActivityPostDetailBinding
import com.example.uaspapb.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Call Function
        fetchData()

        //Bookmark
        binding.btnBookmark.setOnClickListener {

        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this@PostDetailActivity, HomeUserActivity::class.java))
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
                        post = Post(data!!["id"].toString(), data!!["postImage"].toString(), data!!["postTitle"].toString(), data!!["postDescription"].toString())

                        //Set Value
                        Glide.with(binding.ivPostImage)
                            .load(post!!.postImage)
                            .centerCrop()
                            .into(binding.ivPostImage)
                        binding.tvTitleData.text = post!!.postTitle
                        binding.tvDescription.text = post!!.postDescription
                    }
                }
        }
    }
}