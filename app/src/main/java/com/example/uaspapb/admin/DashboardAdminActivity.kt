package com.example.uaspapb.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.Helper
import com.example.uaspapb.MainActivity
import com.example.uaspapb.database.PostBookmarkDao
import com.example.uaspapb.database.PostDatabase
import com.example.uaspapb.database.PostLocalDao
import com.example.uaspapb.databinding.ActivityHomeAdminBinding
import com.example.uaspapb.databinding.LayoutCustomDialogBinding
import com.example.uaspapb.model.Post
import com.example.uaspapb.user.PostAdapterAdmin
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var helper: Helper
    //Firebase
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var postList: ArrayList<Post> = ArrayList<Post>()
    //Room
    private lateinit var mPostBookmarkDao: PostBookmarkDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Helper
        helper = Helper(this@DashboardAdminActivity)

        //Room
        executorService = Executors.newSingleThreadExecutor()
        val db = PostDatabase.getDatabase(this@DashboardAdminActivity.applicationContext)
        mPostBookmarkDao = db!!.postBookmarkDao()!!

        with(binding) {
            //Function Calling
            fetchData()

            //Get user credential
            val username = helper.getUsername()
            tvUsername.text = "Hello $username"

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

        }
    }

    //Function
    private fun fetchData() {
        //Clear previous data
        postList.clear()

        firestore.collection("posts").get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
                showData()
            }
            .addOnFailureListener {exception ->
                Toast.makeText(this@DashboardAdminActivity, "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showData() {
        //RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this@DashboardAdminActivity)
        binding.recyclerView.setHasFixedSize(true)
        val adapter = PostAdapterAdmin(postList)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: PostAdapterAdmin.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@DashboardAdminActivity, DetailAdminActivity::class.java)
                intent.putExtra("EXTID" ,postList[position].id)
                startActivity(intent)
            }

            override fun onEditClick(position: Int) {
                val intent = Intent(this@DashboardAdminActivity, EditAdminActivity::class.java)
                intent.putExtra("EXTID", postList[position].id)
                startActivity(intent)
            }

            override fun onDeleteClick(position: Int) {
                showCustomDialog(position)
            }

        })
    }

    private fun deletePost(position: Int) {
        val id = postList[position].id
        val imageUrl = postList[position].postImage

        firestore.collection("posts").document(id)
            .delete()
            .addOnSuccessListener {
                storage.getReferenceFromUrl(imageUrl)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this@DashboardAdminActivity, "Delete Post Success", Toast.LENGTH_SHORT).show()
                        //Refresh Activity
                        finish()
                        startActivity(intent)
                        //Remove Loading Bar
                        binding.loadingBar.visibility = View.INVISIBLE
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this@DashboardAdminActivity, "Error $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePostBookmark(postId: String) {
        executorService.execute { mPostBookmarkDao.deleteById(postId) }
    }

    //Dialog Alert buat Delete Post
    private fun showCustomDialog(position: Int) {
        val bindingDialog = LayoutCustomDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(this@DashboardAdminActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(bindingDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDialog.tvTitle.text = "Delete Alert"
        bindingDialog.tvMessage.text = "Are you sure want to delete this post?"

        bindingDialog.btnYes.setOnClickListener {
            deletePost(position)
            deletePostBookmark(postList[position].id)
            dialog.dismiss()
            binding.loadingBar.visibility = View.VISIBLE
        }
        bindingDialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}