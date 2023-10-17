package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Adapter.RvAdapterCart
import com.example.bookstores.Model.BookCartModel
import com.example.bookstores.Model.BookHistoryModel
import com.example.bookstores.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Calendar
import kotlin.random.Random

class CartFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var bView: View
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefHistory: DatabaseReference
    private lateinit var mAdapter: RvAdapterCart
    private lateinit var mList: ArrayList<BookCartModel>
    var sum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_cart, container, false)
        dbRefHistory = FirebaseDatabase.getInstance().getReference("BookHistory")
        mList = arrayListOf<BookCartModel>()

        mView.findViewById<Button>(R.id.btnabate).setOnClickListener {
            bottomSheet()
        }
        getSach()

        return mView
    }

    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun bottomSheet() {
        bView = layoutInflater.inflate(R.layout.fragment_new_task_sheet, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bView)
        dialog.show()
        val edtname = bView.findViewById<EditText>(R.id.edtname)
        val edtsdt = bView.findViewById<EditText>(R.id.edtsdt)
        val edtaddress = bView.findViewById<EditText>(R.id.edtaddress)
        val edtmethod = bView.findViewById<EditText>(R.id.edtmethod)
        bView.findViewById<TextView>(R.id.txtsummoney_dialog).text = mView.findViewById<TextView>(R.id.txtsummoney).text.toString()
        bView.findViewById<Button>(R.id.btnabatedialog).setOnClickListener {
            if (edtname.text?.isEmpty() == true || edtsdt.text?.isEmpty() == true
                || edtaddress.text?.isEmpty() == true || edtmethod.text?.isEmpty() == true
            ) {
                if (edtname.text?.isEmpty() == true) {
                    edtname.error = "Vui lòng nhập họ tên"
                }
                if (edtsdt.text?.isEmpty() == true) {
                    edtsdt.error = "Vui lòng nhập số điện thoại"
                }
                if (edtaddress.text?.isEmpty() == true) {
                    edtaddress.error = "Vui lòng nhập địa chỉ"
                }
                if (edtmethod.text?.isEmpty() == true) {
                    edtmethod.error = "Vui lòng nhập phương thức thanh toán"
                }
            } else {
                val id = dbRefHistory.push().key
                val maDon = taoMaDonHang()
                val hoTen = edtname.text.toString()
                val sdt = edtsdt.text.toString()
                val diaChi = edtaddress.text.toString()
                val cartItems = mutableListOf<String>()
                for(i in mList){
                    cartItems.add(i.btitle + " (" + i.bamount + ")")
                }
                val allBook = cartItems.joinToString(", ")
                val calendar = Calendar.getInstance()
                val currentDateTime = calendar.time.toString()
                val tongTien = mView.findViewById<TextView>(R.id.txtsummoney).text.toString()
                val thanhToan = edtmethod.text.toString()
                val book = BookHistoryModel(
                    id, maDon, hoTen, sdt, diaChi, allBook, currentDateTime, tongTien, thanhToan
                )
                dbRefHistory.child(id!!).setValue(book)
                Toast.makeText(requireContext(), "Đặt hàng thành công !", Toast.LENGTH_SHORT).show()
                mList.clear()
                dbRef.removeValue()
                mAdapter.notifyDataSetChanged()
                mView.findViewById<TextView>(R.id.txtsummoney).text = "0.0 VNĐ"
                dialog.dismiss()
            }
        }
    }

    private fun getSach() {
        mView.findViewById<RecyclerView>(R.id.rcvcart).visibility = View.GONE
        mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("BookCart")
        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("CutPasteId")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                if (snapshot.exists()) {
                    for (book in snapshot.children) {
                        val bookData = book.getValue(BookCartModel::class.java)
                        if (bookData != null) {
                            mList.add(bookData)
                        }
                    }
                    sumAbate()
                    //, WeakReference(requireActivity()), requireContext()
                    mAdapter = RvAdapterCart(mList, WeakReference(this@CartFragment))
                    mView.findViewById<RecyclerView>(R.id.rcvcart).visibility = View.VISIBLE
                    mView.findViewById<RecyclerView>(R.id.rcvcart).adapter = mAdapter
                    mView.findViewById<RecyclerView>(R.id.rcvcart).layoutManager =
                        GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
                    mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun sumAbate() {
        sum = 0.0
        for (book in mList) {
            sum += (book.bprice * book.bamount)
        }
        mView.findViewById<TextView>(R.id.txtsummoney).text = sum.toString() + "00 VNĐ"
    }

    @SuppressLint("SetTextI18n")
    fun updatePrice(newPrice: Double) {
        sum = newPrice.toDouble()
        val txtsum = mView.findViewById<TextView>(R.id.txtsummoney)
        txtsum.text = sum.toString() + "VNĐ"
    }

    private fun taoMaDonHang(): String {
        val soChuSo = 5
        val soChuCai = 3
        val soChuCaiCuoi = 1

        val chuSo = "0123456789"
        val chuCai = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val chuCuoi = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        val random = Random(System.currentTimeMillis())

        val maSo = (1..soChuSo).map { chuSo[random.nextInt(chuSo.length)] }.joinToString("")
        val maChuCai = (1..soChuCai).map { chuCai[random.nextInt(chuCai.length)] }.joinToString("")
        val maChuCaiCuoi =
            (1..soChuCaiCuoi).map { chuCuoi[random.nextInt(chuCuoi.length)] }.joinToString("")

        return "$maChuCai$maSo$maChuCaiCuoi"

    }
}
