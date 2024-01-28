package com.example.bookstores.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstores.Adapter.RvAdapterComment
import com.example.bookstores.Fragment.SuccessfulOrderFragment
import com.example.bookstores.Model.CommentModel
import com.example.bookstores.interfaces.Model.BookCartModel
import com.example.bookstores.interfaces.Model.BookHistoryModel
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates
import kotlin.random.Random

class DetailActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var listBook: ArrayList<BookModel>
    private lateinit var listComment: ArrayList<CommentModel>
    private var pos by Delegates.notNull<Int>()
    private var isClick : Boolean = false
    private var isClickLove : Boolean = false
    private var count = 0
    private lateinit var dbRefCart: DatabaseReference
    private lateinit var dbRefFavourite: DatabaseReference
    private lateinit var dbRefComment: DatabaseReference
    private lateinit var dbRefHistory: DatabaseReference
    private lateinit var bView: View
    private lateinit var contentText: TextView
    private lateinit var readMore: TextView
    private lateinit var hideLess: TextView
    lateinit var binding: ActivityDetailBinding
    private lateinit var dialogProgress: Dialog
    private lateinit var edtMess: EditText
    private lateinit var emailUser: String
    private lateinit var mAdapter: RvAdapterComment

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listComment = arrayListOf<CommentModel>()

        dbRefCart = FirebaseDatabase.getInstance().getReference("BookCart")
        dbRefFavourite = FirebaseDatabase.getInstance().getReference("BookFavourite")
        dbRefHistory = FirebaseDatabase.getInstance().getReference("BookHistory")
        dbRefComment = FirebaseDatabase.getInstance().getReference("Comment")

        getBundleData()
        alertDialog()
        bottomSheet()
        getCommentFromFirebase()
        btnSendComment()
        initData()
        btnBack()
        addCartBook()
        addFavouriteBook()
        Navigation()
        btnimgNavigation()
    }

    private fun getBundleData() {
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            listBook = bundle.getParcelableArrayList<BookModel>("bookList") as ArrayList<BookModel>
            emailUser = bundle.getString("emailUser").toString()
            pos = bundle.getInt("pos")
        }
    }

    private fun alertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)
        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đặt hàng !")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()
    }

    @SuppressLint("SetTextI18n")
    private fun btnSendComment(){
        count ++
        findViewById<TextView>(R.id.txtCountComment).text = "($count)"
        edtMess = findViewById(R.id.edtMess)
        val btnSend = findViewById<Button>(R.id.btnSend)
        btnSend.setOnClickListener{
            scrollCommentToBottom()
            addCommentFirebase()
            val message = edtMess.text.toString()
            if (!message.isEmpty() ) {
                edtMess.text.clear()
            }
        }
    }
    private fun addCommentFirebase(){
        val edtsendMessage = findViewById<TextView>(R.id.edtMess).text
        if(edtsendMessage.isNotEmpty() && edtsendMessage.isNotBlank()){
            val id = dbRefComment.push().key
            val titleBook = listBook[pos].btitle
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance().time
            val currentDateTime = dateFormat.format(calendar)
            val comment = edtsendMessage.toString()

            val commentOb = CommentModel(id, titleBook, emailUser, comment, currentDateTime)
            if (id != null) {
                dbRefComment.child(id).setValue(commentOb)
            }
        }
    }
    private fun getCommentFromFirebase(){
        dbRefComment = FirebaseDatabase.getInstance().getReference("Comment")
        dbRefComment.addValueEventListener(object : ValueEventListener{
            @SuppressLint("CutPasteId", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                listComment.clear()
                if(snapshot.exists()){
                    for(cmt in snapshot.children){
                        val cmtData = cmt.getValue(CommentModel::class.java)
                        if(cmtData != null){
                            if(cmtData.titleBook.toString() == listBook[pos].btitle.toString()){
                                listComment.add(cmtData)
                            }
                        }
                    }
                    count = listComment.size
                    findViewById<TextView>(R.id.txtCountComment).text = "(${count})"
                    mAdapter = RvAdapterComment(listComment)
                    findViewById<RecyclerView>(R.id.rcvComment).layoutManager =
                        GridLayoutManager(this@DetailActivity, 1, GridLayoutManager.VERTICAL, false)
                    findViewById<RecyclerView>(R.id.rcvComment).adapter = mAdapter
                    scrollCommentToBottom()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun scrollCommentToBottom() {
        val rcvComment = findViewById<RecyclerView>(R.id.rcvComment)
        val adapter = rcvComment.adapter
        if (adapter != null && adapter.itemCount > 0) {
            val lastItemPosition = adapter.itemCount - 1
            rcvComment.layoutManager?.smoothScrollToPosition(rcvComment, null, lastItemPosition)
        }
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
        findViewById<Button>(R.id.btnbuy).setOnClickListener {
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
                    dialogProgress.dismiss()
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

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val calendar = Calendar.getInstance().time
                        val currentDateTime = dateFormat.format(calendar)

                        val tongTien = findViewById<TextView>(R.id.txtprice).text.toString()
                        val thanhToan = edtmethod.text.toString()
                        val getintent = intent
                        val email = getintent?.getStringExtra("email")

                        val book = BookHistoryModel(id, maDon, hoTen, sdt, diaChi, allBook, currentDateTime, tongTien, thanhToan, email)
                        dbRefHistory.child(id!!).setValue(book)
                        openFragment(SuccessfulOrderFragment())
                        binding.imgback.isEnabled = false
                        binding.imgaddcart.isEnabled = false
                        binding.btnbuy.isEnabled = false
                        binding.imgNav.isEnabled = false
                        dialog.dismiss()
                    }, 1200)
                }
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
                val email = intent.getStringExtra("email")

                val email_title = email + bTitle
                Log.d("email_title", email_title)

                val book = BookCartModel(bId, bTitle, bImage, bAuthor, bNxb, bNumpages, bLoai, bPrice, 1, bDetail, email_title)

                val query = dbRefCart.orderByChild("bemail").equalTo(email_title)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (bookSnapshot in snapshot.children) {
                                val bookData = bookSnapshot.getValue(BookCartModel::class.java)
                                bookData?.let {
                                    val updatedAmount = it.bamount + 1
                                    bookSnapshot.ref.child("bamount").setValue(updatedAmount)
                                }
                            }
                        } else {
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
                val intent = intent
                val email = intent.getStringExtra("email")
                val email_title = email + bTitle
                val book = BookModel(bId, bTitle, bImage, bAuthor, bNxb, bLoai, bNumpages, bPrice, bDetail, email_title)

                val query = dbRefFavourite.orderByChild("bemail").equalTo(email + listBook[pos].btitle)

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
        contentText = findViewById<TextView>(R.id.txtdetail)
        readMore = findViewById<TextView>(R.id.readMoreButton)
        hideLess = findViewById<TextView>(R.id.hidelessButton)
        if (listBook.isNotEmpty() && pos >= 0 && pos < listBook.size) {
            Glide.with(applicationContext)
                .load(listBook[pos].bimg) // Đường dẫn URL của hình ảnh
                .into(findViewById<ImageView>(R.id.imgdetail))
            findViewById<TextView>(R.id.txttitle).text = listBook[pos].btitle.toString()
            findViewById<TextView>(R.id.txtauthor).text = listBook[pos].bauthor.toString()
            findViewById<TextView>(R.id.txtnxb).text = listBook[pos].bnxb.toString()
            findViewById<TextView>(R.id.txtnumpages).text = listBook[pos].bnumpages.toString()
            findViewById<TextView>(R.id.txtloai).text = listBook[pos].bkindOfSach.toString()
            findViewById<TextView>(R.id.txtprice).text = listBook[pos].bprice.toString() + "00 VNĐ"

            contentText.text = listBook[pos].bdetail.toString()
            contentText.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    contentText.viewTreeObserver.removeOnPreDrawListener(this)
                    val lineCount = contentText.lineCount
                    if (lineCount > contentText.maxLines) {
                        readMore.visibility = View.VISIBLE
                    }

                    return true
                }
            })
            findViewById<TextView>(R.id.txtdetailtitle).text = "Tóm Tắt Nội Dung"
        } else {

        }
    }
    fun onReadMoreButtonClick(view: View) {
        contentText.maxLines = Int.MAX_VALUE
        readMore.visibility = View.GONE
        hideLess.visibility = View.VISIBLE
    }
    fun onHideContentButtonClick(view: View) {
        contentText.maxLines = 3
        readMore.visibility = View.VISIBLE
        hideLess.visibility = View.GONE
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.endter_from_right, R.anim.exit_to_right, R.anim.endter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

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