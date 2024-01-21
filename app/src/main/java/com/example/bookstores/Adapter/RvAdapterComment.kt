package com.example.bookstores.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Model.CommentModel
import com.example.bookstores.R

class RvAdapterComment (private var listComment: ArrayList<CommentModel>): RecyclerView.Adapter<RvAdapterComment.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var txtEmail = itemView.findViewById<TextView>(R.id.txtEmail)
        var txtComment = itemView.findViewById<TextView>(R.id.txtComment)
        var txtDateTime = itemView.findViewById<TextView>(R.id.txtDateTime)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_comment_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = listComment[position]
        holder.txtEmail.text = current.emailUser
        holder.txtComment.text = current.comment
        holder.txtDateTime.text = current.dateTime
    }

    override fun getItemCount(): Int {
        return listComment.size
    }
}