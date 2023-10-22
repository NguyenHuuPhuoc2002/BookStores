package com.example.bookstores.Activity.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.example.oder_food_app.Adapter.AdsModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RvAdapterAds (private var listAds: ArrayList<AdsModel>): RecyclerView.Adapter<RvAdapterAds.ViewHolder>(){
    private lateinit var mListener: onItemClickListener
    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    inner class ViewHolder(itemView: View , clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
        var txttitle: TextView = itemView.findViewById(R.id.txttitleAds)
        var imgAds: ImageView = itemView.findViewById(R.id.imgAds)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_ads_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = listAds[position]
        holder.txttitle.text = current.title
        holder.imgAds.setImageResource(current.img)
    }
}