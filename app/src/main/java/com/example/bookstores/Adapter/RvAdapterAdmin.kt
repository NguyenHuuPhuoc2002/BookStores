package com.example.bookstores.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.R
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.interfaces.onItemClickListener

class RvAdapterAdmin(private var listBook: List<BookModel>): RecyclerView.Adapter<RvAdapterAdmin.ViewHolder>() {
    private lateinit var mListener: onItemClickListener
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }
     class ViewHolder(itemView: View, clickListener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        init{
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_book_item_admin, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView")position: Int) {
       holder.itemView.apply {
           findViewById<TextView>(R.id.txttitle).text = listBook[position].btitle
       }
        Glide.with(holder.itemView.context)
            .load(listBook[position].bimg)
            .into(holder.itemView.findViewById(R.id.imgbook))
    }

    override fun getItemCount(): Int {
        return listBook.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(listBook:List<BookModel>){
        this.listBook = listBook
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        listBook = emptyList()
        notifyDataSetChanged()
    }
}