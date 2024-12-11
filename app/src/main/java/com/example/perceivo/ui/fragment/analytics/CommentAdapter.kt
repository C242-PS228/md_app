package com.example.perceivo.ui.fragment.analytics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.perceivo.R
import com.example.perceivo.model.CommentsItem

class CommentAdapter(private val comments: List<CommentsItem>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tv_username)
        val comment: TextView = itemView.findViewById(R.id.tv_comment)
        val profileImage: ImageView = itemView.findViewById(R.id.img_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.username.text = comment.username
        holder.comment.text = comment.text

        if (comment.avatar.isNullOrBlank()) {
            holder.profileImage.setImageResource(R.drawable.ic_placeholder)
        } else {
            Glide.with(holder.itemView.context)
                .load(comment.avatar)
                .placeholder(R.drawable.ic_user_default)
                .error(R.drawable.ic_user_default)
                .circleCrop()
                .into(holder.profileImage)
        }
    }


    override fun getItemCount() = comments.size
}
