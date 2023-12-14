package com.example.uaspapb.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspapb.R
import com.example.uaspapb.model.Post

class DashboardPostAdapter(private val postList: ArrayList<Post>)
    :RecyclerView.Adapter<DashboardPostAdapter.DashboardPostViewHolder> (){

    private lateinit var mListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardPostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_user_item, parent, false)

        return DashboardPostViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: DashboardPostViewHolder, position: Int) {
        val currentItem = postList[position]

        Glide.with(holder.postImage)
            .load(currentItem.postImage)
            .centerCrop()
            .into(holder.postImage)
        holder.postTitle.text = currentItem.postTitle
        holder.postDescription.text = currentItem.postDescription

    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onBookmarkClick(position: Int)
    }

    class DashboardPostViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postDescription: TextView = itemView.findViewById(R.id.postDescription)
        val postContainer: RelativeLayout = itemView.findViewById(R.id.postContainer)
        val btnBookmark: Button = itemView.findViewById(R.id.btnBookmark)
        init {
            postContainer.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            btnBookmark.setOnClickListener {
                listener.onBookmarkClick(adapterPosition)
            }
        }
    }
}

