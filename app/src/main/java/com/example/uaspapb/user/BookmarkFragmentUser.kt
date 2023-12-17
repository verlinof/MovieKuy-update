package com.example.uaspapb.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.R
import com.example.uaspapb.database.PostDao
import com.example.uaspapb.database.PostDatabase
import com.example.uaspapb.database.PostRoom
import com.example.uaspapb.databinding.FragmentBookmarkUserBinding
import com.example.uaspapb.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BookmarkFragmentUser : Fragment() {
    private lateinit var binding: FragmentBookmarkUserBinding
    private var postList: ArrayList<Post> = ArrayList<Post>()
    private var postRoomList: ArrayList<PostRoom> = ArrayList<PostRoom>()
    private var postIdList: ArrayList<String> = ArrayList<String>()
    //Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    //Room
    private lateinit var mPostDao: PostDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkUserBinding.inflate(layoutInflater)
        //Room
        executorService = Executors.newSingleThreadExecutor()
        val db = PostDatabase.getDatabase(requireActivity().applicationContext)
        mPostDao = db!!.postDao()!!

        with(binding) {
            //Get user data
            CoroutineScope(Dispatchers.Main).launch {
                getUserCredential()
            }
            allPostsByEmail(currentUser!!.email!!)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    //Function
    private suspend fun getUserCredential() {
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

                Toast.makeText(requireActivity().applicationContext, "Error : $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        if(postIdList.size !=  0) {
            firestore.collection("posts").whereIn("id", postIdList)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        postList.add(document.toObject(Post::class.java))
                    }
                    //RecyclerView
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                    binding.recyclerView.setHasFixedSize(true)
                    //Adapter RecyclerView
                    val adapter = BookmarkPostAdapter(postList, requireActivity().applicationContext)
                    binding.recyclerView.adapter = adapter
                    adapter?.setOnItemClickListener(object : BookmarkPostAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(requireContext(), PostDetailActivity::class.java)
                            intent.putExtra("EXTID" ,postList[position].id)
                            intent.putExtra("EXTROOMID" ,postRoomList[position].id)
                            intent.putExtra("EXTTYPE" ,"bookmark")
                            startActivity(intent)
                        }

                        override fun onBookmarkClick(position: Int) {
                            deleteBookmark(postRoomList[position])
                            postIdList.clear()
                            postRoomList.clear()
                            postList.clear()
                            replaceFragment(this@BookmarkFragmentUser)
                        }

                    })

                }
                .addOnFailureListener {exception ->
                    Toast.makeText(requireActivity().applicationContext, "Error : $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun allPostsByEmail(emailData: String) {
        try{
            mPostDao.allPostsByUsername(emailData).observe(requireActivity()) {
                for(document in it) {
                    postRoomList.add(document)
                    postIdList.add(document.postId)
                }
                fetchData()
            }
        }catch (e: Exception) {
            Toast.makeText(requireActivity().applicationContext, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, fragment)

        fragmentTransaction.detach(this@BookmarkFragmentUser)
        fragmentTransaction.attach(fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun deleteBookmark(post: PostRoom) {
        executorService.execute { mPostDao.delete(post) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookmarkFragmentUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookmarkFragmentUser().apply {
                arguments = Bundle().apply {
                }
            }
    }
}