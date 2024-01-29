package com.example.bookstores.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.R
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.FirebaseDatabase

class RvAdapterAdmin(private var listBook: List<BookModel>): RecyclerView.Adapter<RvAdapterAdmin.ViewHolder>() {
    private lateinit var mListener: onItemClickListener
    private lateinit var dialogProgress: Dialog
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

        holder.itemView.findViewById<ImageView>(R.id.imgdelete).setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Xác nhận xóa")
            alertDialogBuilder.setMessage("Bạn có muốn xóa ${listBook[position].btitle} không?")

            alertDialogBuilder.setPositiveButton("Có") { dialog: DialogInterface, _: Int ->
                // Xử lý khi người dùng chọn "Có"
                val alertDialog = AlertDialog.Builder(holder.itemView.context)
                val progressBar = ProgressBar(holder.itemView.context)

                alertDialog.setView(progressBar)
                alertDialog.setTitle("Đang xóa !")
                alertDialog.setCancelable(false)
                dialogProgress = alertDialog.create()
                dialogProgress.show()

                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    deleteItemFirebase(listBook[position].bid)
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
    private fun deleteItemFirebase(id: String?) {
        val dbRef = FirebaseDatabase.getInstance().getReference("BookHome").child(id!!)
        dbRef.removeValue()
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