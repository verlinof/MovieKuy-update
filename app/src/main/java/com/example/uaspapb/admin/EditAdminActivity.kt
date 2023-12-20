package com.example.uaspapb.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.uaspapb.databinding.ActivityEditAdminBinding
import com.example.uaspapb.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private var uri: Uri? = null
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            //Call Function
            fetchData()

            btnSelectImage.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                pickImage.launch(intent)
            }
            btnEdit.setOnClickListener {
                loadingBar.visibility = View.VISIBLE
                updateData()
            }
            btnBack.setOnClickListener {
                startActivity(Intent(this@EditAdminActivity, DashboardAdminActivity::class.java))
                finishAffinity()
            }
        }
    }

    //Function
    //Open Image Gallery
    val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == RESULT_OK) {
            uri = result.data?.data
            //Set Image
            binding.ivPostImage.setImageURI(uri)
        }
    }

    //Fetch Data untuk isi Edit Text
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
                        binding.etTitle.setText(post!!.postTitle)
                        binding.etDescription.setText(post!!.postDescription)
                    }
                }
        }
    }

    //Update data dengan image baru
    private fun updateData() {
        if(uri != null) {
            //Delete old files
            storage.getReferenceFromUrl(post!!.postImage)
                .delete()

            //Update data to firebase
            val uploadFile = storageReference.child("posts/" + System.currentTimeMillis())
            val uploadTask = uploadFile.putFile(uri!!)

            uploadTask.addOnSuccessListener {
                uploadFile.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val postTitle = binding.etTitle.text.toString()
                    val postDescription = binding.etDescription.text.toString()
                    val postData = Post(
                        id = post!!.id,
                        postDescription = postDescription,
                        postImage = downloadUrl,
                        postTitle = postTitle
                    )

                    //Update Firestore
                    firestore.collection("posts")
                        .document(post!!.id)
                        .set(postData)
                        .addOnFailureListener {error ->
                            Toast.makeText(this@EditAdminActivity, "Error : $error", Toast.LENGTH_SHORT).show()
                        }
                    binding.loadingBar.visibility = View.INVISIBLE
                    Toast.makeText(this@EditAdminActivity, "Upload Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditAdminActivity, DashboardAdminActivity::class.java))
                    finishAffinity()
                }
            }
        } else{
            updateDataNoImage()
        }

    }

    //Update data tanpa image baru
    private fun updateDataNoImage() {
        val postTitle = binding.etTitle.text.toString()
        val postDescription = binding.etDescription.text.toString()
        val postData = Post(
            id = post!!.id,
            postDescription = postDescription,
            postImage = post!!.postImage,
            postTitle = postTitle
        )

        //Update Firestore
        firestore.collection("posts")
            .document(post!!.id)
            .set(postData)
            .addOnFailureListener {error ->
                Toast.makeText(this@EditAdminActivity, "Error : $error", Toast.LENGTH_SHORT).show()
            }
        binding.loadingBar.visibility = View.INVISIBLE
        Toast.makeText(this@EditAdminActivity, "Update Success", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@EditAdminActivity, DashboardAdminActivity::class.java))
        finishAffinity()
    }
}
