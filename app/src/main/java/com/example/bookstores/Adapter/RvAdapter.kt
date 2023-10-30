package com.example.bookstores.Activity.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RvAdapter (private var listBook: List<BookModel>): RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_book_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return listBook.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.txttitle).text = listBook[position].btitle
            findViewById<TextView>(R.id.txtprice).text = listBook[position].bprice.toString() + "00 VNĐ"
        }
        Glide.with(holder.itemView.context)
            .load(listBook[position].bimg)
            .into(holder.itemView.findViewById(R.id.imgbook))

        holder.itemView.findViewById<ImageView>(R.id.imglove).setOnClickListener {
            holder.itemView.findViewById<ImageView>(R.id.imglove).setImageResource(R.drawable.ic_favorite)
            val dbRefFavourite = FirebaseDatabase.getInstance().getReference("BookFavourite")
            val bTitle = listBook[position].btitle

            val query = dbRefFavourite.orderByChild("btitle").equalTo(bTitle)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                    } else {
                        val bId = dbRefFavourite.push().key
                        val bImage = listBook[position].bimg
                        val bAuthor = listBook[position].bauthor
                        val bNxb = listBook[position].bnxb
                        val bNumpages = listBook[position].bnumpages
                        val bLoai = listBook[position].bkindOfSach
                        val bPrice = listBook[position].bprice
                        val bDetail = listBook[position].bdetail

                        val book = BookModel(bId, bTitle, bImage, bAuthor, bNxb, bNumpages,bLoai, bPrice, bDetail)
                        dbRefFavourite.child(bId!!).setValue(book)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

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