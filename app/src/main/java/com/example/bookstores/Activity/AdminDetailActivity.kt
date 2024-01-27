package com.example.bookstores.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityAdminBinding
import com.example.bookstores.databinding.ActivityAdminDetailBinding
import com.example.bookstores.interfaces.Model.BookModel
import kotlin.properties.Delegates

class AdminDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDetailBinding
    private lateinit var listBook: ArrayList<BookModel>
    private var pos by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundleData()
        dataAssignment()
        btnBack()
    }
    private fun btnBack(){
        binding.imgback.setOnClickListener {
            finish()
        }
    }
    private fun dataAssignment() {
        if (listBook.isNotEmpty() && pos >= 0 && pos < listBook.size) {
            Glide.with(applicationContext)
                .load(listBook[pos].bimg) // Đường dẫn URL của hình ảnh
                .into(findViewById<ImageView>(R.id.imgBookAdmin))
            binding.edtAuthor.setText(listBook[pos].bauthor.toString())
            binding.edtNumpages.setText(listBook[pos].bnumpages.toString())
            binding.edtPrice.setText(listBook[pos].bprice.toString())
            binding.edtNxb.setText(listBook[pos].bnxb.toString())
            binding.edtMtContent.setText(listBook[pos].bdetail.toString())
            binding.edtTitle.setText(listBook[pos].btitle.toString())
        }
    }

    private fun getBundleData() {
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            listBook = bundle.getParcelableArrayList<BookModel>("bookList") as ArrayList<BookModel>
            pos = bundle.getInt("pos")
        }
    }
}