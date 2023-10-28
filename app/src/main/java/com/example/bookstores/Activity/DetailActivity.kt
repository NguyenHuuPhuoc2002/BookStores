package com.example.bookstores.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.bookstores.Fragment.HomeFragment
import com.example.bookstores.Model.BookCartModel
import com.example.bookstores.Model.BookHistoryModel
import com.example.bookstores.Model.BookModel
import com.example.bookstores.Model.TaskViewModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityDetailBinding
import com.example.bookstores.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import kotlin.properties.Delegates
import kotlin.random.Random

class DetailActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var listBook: ArrayList<BookModel>
    private var pos by Delegates.notNull<Int>()
    private var isClick : Boolean = false
    private var isClickLove : Boolean = false
    private lateinit var dbRefCart: DatabaseReference
    private lateinit var dbRefFavourite: DatabaseReference
    private lateinit var dbRefHistory: DatabaseReference
    private lateinit var bView: View
    private lateinit var binding: ActivityDetailBinding
    private lateinit var dialogProgress: Dialog

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đặt hàng !")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()

        dbRefCart = FirebaseDatabase.getInstance().getReference("BookCart")
        dbRefFavourite = FirebaseDatabase.getInstance().getReference("BookFavourite")
        dbRefHistory = FirebaseDatabase.getInstance().getReference("BookHistory")
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            listBook = bundle.getParcelableArrayList<BookModel>("bookList") as ArrayList<BookModel>
        }
        pos = bundle?.getInt("pos")!!
        findViewById<Button>(R.id.btnbuy).setOnClickListener {
            bottomSheet()
        }
        initData()
        btnBack()
        addCartBook()
        addFavouriteBook()
        Navigation()
        btnimgNavigation()
    }

    private fun Navigation(){
        findViewById<NavigationView>(R.id.navigation_drawer).setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> finish()
                R.id.nav_out -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            binding.drawLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun btnimgNavigation(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.draw_layout)
        val navView = findViewById<NavigationView>(R.id.navigation_drawer)
        binding.imgNav.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }

    @SuppressLint("CutPasteId")
    private fun bottomSheet() {
        bView = layoutInflater.inflate(R.layout.fragment_new_task_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bView)
        dialog.show()
        val edtname = bView.findViewById<EditText>(R.id.edtname)
        val edtsdt = bView.findViewById<EditText>(R.id.edtsdt)
        val edtaddress = bView.findViewById<EditText>(R.id.edtaddress)
        val edtmethod = bView.findViewById<EditText>(R.id.edtmethod)
        bView.findViewById<TextView>(R.id.txtsummoney_dialog).text = findViewById<TextView>(R.id.txtprice).text.toString()
        bView.findViewById<Button>(R.id.btnabatedialog).setOnClickListener {
            dialogProgress.show()

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
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    dialogProgress.dismiss()
                    val id = dbRefHistory.push().key
                    val maDon = taoMaDonHang()
                    val hoTen = edtname.text.toString()
                    val sdt = edtsdt.text.toString()
                    val diaChi = edtaddress.text.toString()
                    val allBook = findViewById<TextView>(R.id.txttitle).text.toString()
                    val calendar = Calendar.getInstance()
                    val currentDateTime = calendar.time.toString()
                    val tongTien = findViewById<TextView>(R.id.txtprice).text.toString()
                    val thanhToan = edtmethod.text.toString()
                    val book = BookHistoryModel(
                        id, maDon, hoTen, sdt, diaChi, allBook, currentDateTime, tongTien, thanhToan
                    )
                    dbRefHistory.child(id!!).setValue(book)
                    Toast.makeText(this, "Đặt hàng thành công !", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }, 1200)
            }
        }
    }

    private fun addCartBook(){
        findViewById<ImageView>(R.id.imgaddcart).setOnClickListener {
            if(!isClick)
            {
                findViewById<ImageView>(R.id.imgaddcart).setBackgroundResource(R.drawable.bg_custombtnadded)
                val bId = dbRefCart.push().key
                val bTitle = listBook[pos].btitle
                val bImage = listBook[pos].bimg
                val bAuthor = listBook[pos].bauthor
                val bNxb = listBook[pos].bnxb
                val bNumpages = listBook[pos].bnumpages
                val bLoai = listBook[pos].bkindOfSach
                val bPrice = listBook[pos].bprice
                val bDetail = listBook[pos].bdetail

                val book = BookCartModel(bId, bTitle, bImage, bAuthor, bNxb, bNumpages, bLoai, bPrice, 1, bDetail )

                val query = dbRefCart.orderByChild("btitle").equalTo(listBook[pos].btitle)

                query.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (book in snapshot.children) {
                                val bookData = book.getValue(BookCartModel::class.java)
                                bookData?.let {
                                    val updatedAmount = it.bamount + 1
                                    book.child("bamount").ref.setValue(updatedAmount)
                                    return
                                }
                            }
                        }else{
                            dbRefCart.child(bId!!).setValue(book)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                isClick = true
            }
        }
    }
    private fun addFavouriteBook(){
        findViewById<ImageView>(R.id.imglove).setOnClickListener {
            if(!isClickLove)
            {
                findViewById<ImageView>(R.id.imglove).setImageResource(R.drawable.ic_favorite)
                val bId = dbRefFavourite.push().key
                val bTitle = listBook[pos].btitle
                val bImage = listBook[pos].bimg
                val bAuthor = listBook[pos].bauthor
                val bNxb = listBook[pos].bnxb
                val bNumpages = listBook[pos].bnumpages
                val bLoai = listBook[pos].bkindOfSach
                val bPrice = listBook[pos].bprice
                val bDetail = listBook[pos].bdetail

                val book = BookModel(bId, bTitle, bImage, bAuthor, bNxb, bLoai, bNumpages, bPrice, bDetail )

                val query = dbRefFavourite.orderByChild("btitle").equalTo(listBook[pos].btitle)

                query.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){

                        }else{
                            dbRefFavourite.child(bId!!).setValue(book)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                isClickLove = true
            }
        }
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
    private fun btnBack(){
        findViewById<ImageView>(R.id.imgback).setOnClickListener {
            finish()
        }
    }
    // Lưu trạng thái yêu thích của cuốn sách
    @SuppressLint("SetTextI18n")
    private fun initData() {
        Glide.with(applicationContext)
            .load(listBook[pos].bimg) // Đường dẫn URL của hình ảnh
            .into(findViewById<ImageView>(R.id.imgdetail))
        findViewById<TextView>(R.id.txttitle).text = listBook[pos].btitle.toString()
        findViewById<TextView>(R.id.txtauthor).text = listBook[pos].bauthor.toString()
        findViewById<TextView>(R.id.txtnxb).text = listBook[pos].bnxb.toString()
        findViewById<TextView>(R.id.txtnumpages).text = listBook[pos].bnumpages.toString()
        findViewById<TextView>(R.id.txtloai).text = listBook[pos].bkindOfSach.toString()
        findViewById<TextView>(R.id.txtprice).text = listBook[pos].bprice.toString() + "00 VNĐ"
        findViewById<TextView>(R.id.txtdetail).text = listBook[pos].bdetail.toString()
        findViewById<TextView>(R.id.txtdetailtitle).text = "Tóm Tắt Nội Dung"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_out -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        binding.drawLayout.closeDrawer(GravityCompat.START)
        return true
    }

}