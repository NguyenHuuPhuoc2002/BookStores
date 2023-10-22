package com.example.oder_food_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.R

class PhotoAdapter(private var mListPhoto: List<PhotoModel>)  : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgphoto: ImageView = itemView.findViewById(R.id.img_photo) // Đảm bảo ánh xạ đúng ImageView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.layout_photo_slide, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(mListPhoto != null){
            return mListPhoto.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo:PhotoModel = mListPhoto.get(position)
        if(photo == null){
            return
        }
        holder.imgphoto.setImageResource(photo.resoureId)
    }
}