package com.example.bookstores.Activity.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.Fragment.CartFragment
import com.example.bookstores.Model.BookCartModel
import com.example.bookstores.R
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference
import java.util.ArrayList

//, WeakReference(this@CartFragment) , private val context: Context
class RvAdapterCart(private val listBook: ArrayList<BookCartModel>, private val activityRef:WeakReference<CartFragment>): RecyclerView.Adapter<RvAdapterCart.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imgv: ImageView = itemView.findViewById(R.id.imgcart)
        var txttitle: TextView = itemView.findViewById(R.id.txttitlecart)
        var txtprice: TextView = itemView.findViewById(R.id.txtpricecart)
        var btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
        var btnPluss: ImageButton = itemView.findViewById(R.id.btnplus)
        var btnMinus: ImageButton = itemView.findViewById(R.id.btnminus)
        var edtQuantity: TextView = itemView.findViewById(R.id.edtQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listBook.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = listBook[position]
        holder.txttitle.text = current.btitle
        holder.txtprice.text = "${current.bprice}00 VNĐ"
        holder.edtQuantity.text = current.bamount.toString()
        Glide.with(holder.itemView.context)
            .load(current.bimg)
            .into(holder.imgv)

        val edtQuantity = holder.itemView.findViewById<EditText>(R.id.edtQuantity)
        edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull()
                if (quantity != null) {
                    updateBook(current.bid!!, current.btitle!!, current.bimg!!, current.bauthor!!, current.bnxb!!,
                        current.bnumpages!!, current.bkindOfSach!!, current.bprice, quantity, current.bdetail!!)
                }
            }
        })


        // Button Plus
        holder.btnPluss.setOnClickListener {
            val newAmount = current.bamount + 1
            updateBook(current.bid!!, current.btitle!!, current.bimg!!, current.bauthor!!, current.bnxb!!,
                current.bnumpages!!, current.bkindOfSach!!, current.bprice, newAmount, current.bdetail!!)
            current.bamount = newAmount
            holder.edtQuantity.text = newAmount.toString()

            /*val activity = activityRef.get()
            if (activity != null) {
                val newTotalPrice = activity.sum + listBook[position].bprice
                activity.updatePrice(newTotalPrice)
            }*/
        }
        // Button Minus
        holder.btnMinus.setOnClickListener {
            if(current.bamount  > 1){
                val newAmount = current.bamount - 1
                updateBook(current.bid!!, current.btitle!!, current.bimg!!, current.bauthor!!, current.bnxb!!, current.bnumpages!!,
                    current.bkindOfSach!!, current.bprice, newAmount, current.bdetail!!)
                current.bamount = newAmount
                holder.edtQuantity.text = newAmount.toString()
            }
        }
        // Button delete
        holder.btnDelete.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Xác nhận xóa")
            alertDialogBuilder.setMessage("Bạn có muốn xóa ${current.btitle} không?")

            alertDialogBuilder.setPositiveButton("Có") { dialog: DialogInterface, _: Int ->
                // Xử lý khi người dùng chọn "Có"
                deleteItemFirebase(current.bid, position)
                notifyItemRemoved(position)
                dialog.dismiss()
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
        if(listBook.size < 1){
            val dbRef = FirebaseDatabase.getInstance().getReference("BookCart").child(id!!)
            dbRef.removeValue()
            val activity = activityRef.get()
            if (activity != null) {
                val newTotalPrice = 0.0
                activity.updatePrice(newTotalPrice)
            }
            if (pos != null) {
               notifyDataSetChanged()
            }
        }else{
            val dbRef = FirebaseDatabase.getInstance().getReference("BookCart").child(id!!)
            dbRef.removeValue()
        }
    }

    private fun updateBook(id: String, title: String, img: String, author: String, nxb: String, numpages: String,
                           loai: String, price: Double, amount: Int, detail: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("BookCart").child(id)
        val bookInfo = BookCartModel(id, title, img, author, nxb, numpages, loai, price, amount, detail)
        dbRef.setValue(bookInfo)
    }
}