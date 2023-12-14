package com.example.uaspapb.admin

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.uaspapb.databinding.ActivityUploadAdminBinding
import com.example.uaspapb.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadAdminBinding
    private lateinit var storageReference: StorageReference
    private val fireStore = FirebaseFirestore.getInstance()
    private var uri: Uri? = Uri.parse("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().reference

        with(binding) {
            btnSelectImage.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                pickImage.launch(intent)
            }
            btnUpload.setOnClickListener {
                loadingBar.visibility = View.VISIBLE
                uploadData()
            }
        }
    }

    val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if(result.resultCode == RESULT_OK) {
            uri = result.data?.data
            //Set Image
            binding.ivPostImage.setImageURI(uri)
        }
    }

    private fun uploadData() {
        //Upload to firebase
        val uploadFile = storageReference.child("posts/" + System.currentTimeMillis())
        val uploadTask = uploadFile.putFile(uri!!)

        uploadTask.addOnSuccessListener {
            uploadFile.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                val postTitle = binding.etTitle.text.toString()
                val postDescription = binding.etDescription.text.toString()
                val post = Post(postImage = downloadUrl, postTitle = postTitle, postDescription = postDescription)

                fireStore.collection("posts")
                    .add(post)
                    .addOnSuccessListener {documentReference ->
                        val postId = documentReference.id
                        post.id = postId
                        documentReference.set(post)
                            .addOnFailureListener { error ->
                                Toast.makeText(this@UploadAdminActivity, "Error : $error", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {error ->
                        Toast.makeText(this@UploadAdminActivity, "Error : $error", Toast.LENGTH_SHORT).show()
                    }
                binding.loadingBar.visibility = View.INVISIBLE
                Toast.makeText(this@UploadAdminActivity, "Upload Success", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


}