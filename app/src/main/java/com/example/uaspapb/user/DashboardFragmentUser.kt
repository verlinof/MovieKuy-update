package com.example.uaspapb.user

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.Helper
import com.example.uaspapb.database.PostBookmark
import com.example.uaspapb.database.PostBookmarkDao
import com.example.uaspapb.database.PostDatabase
import com.example.uaspapb.database.PostLocal
import com.example.uaspapb.database.PostLocalDao
import com.example.uaspapb.databinding.FragmentDashboardUserBinding
import com.example.uaspapb.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DashboardFragmentUser : Fragment() {
    private lateinit var binding: FragmentDashboardUserBinding
    private var postList: ArrayList<Post> = ArrayList<Post>()
    private lateinit var helper: Helper
    //Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private var username: String? = null
    //Room
    private lateinit var mPostBookmarkDao: PostBookmarkDao
    private lateinit var mPostLocalDao: PostLocalDao
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
        binding = FragmentDashboardUserBinding.inflate(inflater)

        //Helper
        helper = Helper(requireContext())

        with(binding) {
            //Room
            executorService = Executors.newSingleThreadExecutor()
            val db = PostDatabase.getDatabase(requireContext())
            mPostBookmarkDao = db!!.postBookmarkDao()!!
            mPostLocalDao = db.postLocalDao()!!

            CoroutineScope(Dispatchers.Main).launch {
                //Get User Credential
                val username = helper.getUsername()
                binding.tvUsername.text = "Hello $username"
            }

            //Check internet and Fetch Data
            if(isInternetAvailable(requireActivity())) {
                fetchData()
                Toast.makeText(requireActivity(), "Establishing Connection", Toast.LENGTH_SHORT).show()
            }else{
                fetchDataOffline()
                Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

        }
        //Binding
        return binding.root
    }

    //Function
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun fetchData() {
        //Remove all data from Room Database
        truncatePostLocal()

        firestore.collection("posts").get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    //Online
                    val post = document.toObject(Post::class.java)
                    postList.add(post)

                    //Insert ke Room buat nanti data offline
                    val postLocal = PostLocal(
                        id = post.id,
                        postDescription = post.postDescription,
                        postImage = post.postImage,
                        postTitle = post.postTitle
                    )
                    insertPostsLocal(postLocal)
                }
                //Update recyclerview
                showData()
            }
            .addOnFailureListener {exception ->
                Toast.makeText(requireActivity(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchDataOffline() {
        //Clear previous data
        postList.clear()

        //Logic
        mPostLocalDao.allPostsLocal().observe(requireActivity()) {posts ->
            for(post in posts) {
                val data = Post(
                    id = post.id,
                    postDescription = post.postDescription,
                    postImage = post.postImage,
                    postTitle = post.postTitle
                )
                postList.add(data)
            }
            //Update recyclerview
            showData()
        }
    }

    private fun showData() {
        //RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        //Adapter RecyclerView
        val adapter = DashboardPostAdapter(postList)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : DashboardPostAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra("EXTID" ,postList[position].id)
                intent.putExtra("EXTTYPE" ,"dashboard")
                startActivity(intent)
            }

            override fun onBookmarkClick(position: Int) {
                val postRoomData = PostBookmark(postId =  postList[position].id, email = currentUser!!.email!!)
                try{
                    insert(postRoomData)
                    Toast.makeText(requireActivity(), "Add to Bookmark Success", Toast.LENGTH_SHORT).show()
                }catch (e: Exception) {
                    Toast.makeText(requireActivity(), "Error : $e", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    //Room Database
    private fun insert(post: PostBookmark) {
        executorService.execute { mPostBookmarkDao.insert(post) }
    }

    private fun insertPostsLocal(post: PostLocal) {
        executorService.execute { mPostLocalDao.insert(post) }
    }

    private fun truncatePostLocal() {
        executorService.execute { mPostLocalDao.truncateTable() }
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