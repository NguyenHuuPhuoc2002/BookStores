package com.example.bookstores.Activity.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Fragment.HistoryFragment
import com.example.bookstores.Model.BookHistoryModel
import com.example.bookstores.R
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference

class RvAdapterHistory(val listBook: ArrayList<BookHistoryModel>, private val activityRef:WeakReference<HistoryFragment>):RecyclerView.Adapter<RvAdapterHistory.BookHolder>() {

    inner class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btn_delete: ImageButton = itemView.findViewById(R.id.btn_delete)
        var txtmadon: TextView = itemView.findViewById(R.id.txtmadon)
        var txthoten: TextView = itemView.findViewById(R.id.txthoten)
        var txtsdt: TextView = itemView.findViewById(R.id.txtsdt)
        var txtdiachi: TextView = itemView.findViewById(R.id.txtdiachi)
        var txtthucdon: TextView = itemView.findViewById(R.id.txtthucdon)
        var txtngaydathang: TextView = itemView.findViewById(R.id.txtngaydathang)
        var txttongtien: TextView = itemView.findViewById(R.id.txttongtien)
        var txtthanhtoan: TextView = itemView.findViewById(R.id.txtthanhtoan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.layout_history_item, parent,false)
        return BookHolder(mView)
    }

    override fun getItemCount(): Int {
        return listBook.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val currentItem = listBook[position]

        holder.txtmadon.text = currentItem.maDon
        holder.txthoten.text = currentItem.hoTen
        holder.txtsdt.text = currentItem.sdt
        holder.txtdiachi.text = currentItem.diaChi
        holder.txtthucdon.text = currentItem.allBook
        holder.txtngaydathang.text = currentItem.ngayDat
        holder.txttongtien.text = currentItem.tongTien.toString()
        holder.txtthanhtoan.text = currentItem.thanhToan

        holder.btn_delete.setOnClickListener {
            val pos = holder.adapterPosition
            if(pos != RecyclerView.NO_POSITION){
                val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
                alertDialogBuilder.setTitle("Xác nhận xóa")
                alertDialogBuilder.setMessage("Bạn có muốn xóa đơn hàng này không ?")
                alertDialogBuilder.setPositiveButton("Có") { dialog: DialogInterface, _:Int ->
                    deleteItemData(currentItem.id, pos)
                    Toast.makeText(holder.itemView.context, "Xóa thành công !", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(position)
                    dialog.dismiss()
                }
                alertDialogBuilder.setNegativeButton("Không"){ dialog: DialogInterface, _:Int ->
                    dialog.dismiss()
                }
                alertDialogBuilder.setCancelable(false)
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    private fun deleteItemData(id: String?, pos: Int?) {
        val dbRef = FirebaseDatabase.getInstance().getReference("BookHistory").child(id!!)
        dbRef.removeValue()
    }

}