package com.example.uaspapb.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspapb.R
import com.example.uaspapb.model.Post

class PostAdapterAdmin(private val postList: ArrayList<Post>)
    :RecyclerView.Adapter<PostAdapterAdmin.PostViewHolder> (){

    private lateinit var mListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_admin_item, parent, false)

        return PostViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postList[position]

        Glide.with(holder.postImage)
            .load(currentItem.postImage)
            .centerCrop()
            .into(holder.postImage)
        holder.postTitle.text = currentItem.postTitle
        if(currentItem.postDescription.length > 75) {
            holder.postDescription.text = currentItem.postDescription.subSequence(0, 75)
        }else{
            holder.postDescription.text = currentItem.postDescription
        }
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    class PostViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postDescription: TextView = itemView.findViewById(R.id.postDescription)
        val postContainer: RelativeLayout = itemView.findViewById(R.id.postContainer)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        init {
            postContainer.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            btnEdit.setOnClickListener {
                listener.onEditClick(adapterPosition)
            }
            btnDelete.setOnClickListener {
                listener.onDeleteClick(adapterPosition)
            }
        }
    }
}

