package com.example.bookstores.Activity.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.Activity.MainActivity
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference

class RvAdapterFavourite (private var listBook: List<BookModel>): RecyclerView.Adapter<RvAdapterFavourite.ViewHolder>(){
    private lateinit var mListener: onItemClickListener
    private lateinit var dbRef: DatabaseReference
    private lateinit var dialogProgress: Dialog
    private lateinit var activityRef: WeakReference<MainActivity>
    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    inner class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
        var txttitle: TextView = itemView.findViewById(R.id.txttitle)
        var imgdelete: ImageView = itemView.findViewById(R.id.imgdelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_favourite_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return listBook.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = listBook[position]
        activityRef = WeakReference(holder.itemView.context as MainActivity)
        holder.txttitle.text = listBook[position].btitle

        Glide.with(holder.itemView.context)
            .load(listBook[position].bimg)
            .into(holder.itemView.findViewById(R.id.imgbook))

        holder.imgdelete.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Xác nhận xóa")
            alertDialogBuilder.setMessage("Bạn có muốn xóa ${current.btitle} không?")

            alertDialogBuilder.setPositiveButton("Có") { dialog: DialogInterface, _: Int ->
                val alertDialog = AlertDialog.Builder(holder.itemView.context)
                val progressBar = ProgressBar(holder.itemView.context)

                alertDialog.setView(progressBar)
                alertDialog.setTitle("Đang xóa !")
                alertDialog.setCancelable(false)
                dialogProgress = alertDialog.create()
                dialogProgress.show()

                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    deleteItemFirebase(current.bid, position)
                    notifyItemRemoved(position)
                    Toast.makeText(holder.itemView.context, "Xóa thành công !", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    dialogProgress.dismiss()
                }, 1000)
            }

            alertDialogBuilder.setNegativeButton("Không") { dialog: DialogInterface, _: Int ->
                // Xử lý khi người dùng chọn "Không"
                dialog.dismiss()
            }
            alertDialogBuilder.setCancelable(false)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteItemFirebase(id: String?, pos: Int?) {
        if(listBook.isEmpty()){
            dbRef = FirebaseDatabase.getInstance().getReference("BookFavourite").child(id!!)
            dbRef.removeValue()
            val activity = activityRef.get()
            if (activity != null) {
                activity.binding.txtNumFav.text = "0"
                notifyDataSetChanged()
            }
            notifyDataSetChanged()
        }else{
            dbRef = FirebaseDatabase.getInstance().getReference("BookFavourite").child(id!!)
            dbRef.removeValue()
        }
    }
}