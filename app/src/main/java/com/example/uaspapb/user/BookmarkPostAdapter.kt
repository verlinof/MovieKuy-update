package com.example.uaspapb.user

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspapb.R
import com.example.uaspapb.model.Post

class BookmarkPostAdapter(private val postList: ArrayList<Post>, private val context: Context)
    :RecyclerView.Adapter<BookmarkPostAdapter.BookmarkPostViewHolder> (){

    private lateinit var mListener: onItemClickListener


    override fun onBindViewHolder(
        holder: BookmarkPostViewHolder,
        position: Int
    ) {
        val currentItem = postList[position]

        //Set button to remove bookmark
        holder.btnBookmark.text = "Remove Bookmark"

        val color = ContextCompat.getColor(context, R.color.red)
        val colorStateList = ColorStateList.valueOf(color)

        ViewCompat.setBackgroundTintList(holder.btnBookmark, colorStateList)

        Glide.with(holder.postImage)
            .load(currentItem.postImage)
            .centerCrop()
            .into(holder.postImage)
        holder.postTitle.text = currentItem.postTitle
        holder.postDescription.text = currentItem.postDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkPostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_user_item, parent, false)

        return BookmarkPostViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onBookmarkClick(position: Int)
    }

    class BookmarkPostViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
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

