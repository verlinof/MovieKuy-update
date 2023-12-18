package com.example.uaspapb.user

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.example.uaspapb.R
import com.example.uaspapb.database.PostBookmark
import com.example.uaspapb.database.PostBookmarkDao
import com.example.uaspapb.database.PostDatabase
import com.example.uaspapb.databinding.ActivityPostDetailBinding
import com.example.uaspapb.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private var post: Post? = null
    //
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = Firebase.auth.currentUser
    //Room
    private lateinit var mPostBookmarkDao: PostBookmarkDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Room
        executorService = Executors.newSingleThreadExecutor()
        val db = PostDatabase.getDatabase(this@PostDetailActivity)
        mPostBookmarkDao = db!!.postBookmarkDao()!!

        //Call Function
        fetchData()

        val bundle: Bundle? = intent.extras
        val id = bundle?.getInt("EXTROOMID")
        val postIdData = bundle?.getString("EXTID")
        val type = bundle?.getString("EXTTYPE")

        if(type == "dashboard") {
            //Bookmark
            binding.btnBookmark.setOnClickListener {
                insert(
                    PostBookmark(
                        postId = postIdData!!,
                        email = currentUser!!.email!!
                    )
                )
                Toast.makeText(this@PostDetailActivity, "Add to Bookmark Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@PostDetailActivity, HomeUserActivity::class.java))
                finishAffinity()
            }
            binding.btnBack.setOnClickListener {
                startActivity(Intent(this@PostDetailActivity, HomeUserActivity::class.java))
                finishAffinity()
            }
        }else {
            //Set Bookmark Style
            binding.btnBookmark.text = "Remove Bookmark"
            val color = ContextCompat.getColor(this@PostDetailActivity, R.color.red)
            val colorStateList = ColorStateList.valueOf(color)

            ViewCompat.setBackgroundTintList(binding.btnBookmark, colorStateList)

            //Bookmark
            binding.btnBookmark.setOnClickListener {
                deleteBookmark(
                    PostBookmark(
                        id = id!!,
                        postId = post!!.id,
                        email = currentUser!!.email!!
                    )
                )
                Toast.makeText(this@PostDetailActivity, "Remove Bookmark Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@PostDetailActivity, HomeUserActivity::class.java))
                finishAffinity()
            }
            binding.btnBack.setOnClickListener {
                startActivity(Intent(this@PostDetailActivity, HomeUserActivity::class.java))
                finishAffinity()
            }
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
                    }
                }
        }
    }

    private fun insert(post: PostBookmark) {
        executorService.execute { mPostBookmarkDao.insert(post) }
    }

    private fun deleteBookmark(post: PostBookmark) {
        executorService.execute { mPostBookmarkDao.delete(post) }
    }

}