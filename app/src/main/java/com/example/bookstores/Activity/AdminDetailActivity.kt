package com.example.bookstores.Activity

import android.app.AlertDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityAdminDetailBinding
import com.example.bookstores.interfaces.Model.BookModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.properties.Delegates

class AdminDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDetailBinding
    private lateinit var listBook: ArrayList<BookModel>
    private var filePath: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var dialogProgress: AlertDialog
    private var pos by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listBook = arrayListOf()
        dbRef = FirebaseDatabase.getInstance().getReference("BookHome")
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        getBundleData()
        showButton()
        dataAssignment()
        btnBack()
        btnAddBook()
        btnUpdateBook()
        btnChooseImg()
        alertDialog()
    }

    private fun btnUpdateBook() {
        binding.btnUpdateBook.setOnClickListener {
            val id = listBook[pos].bid
            val title = binding.edtTitle.text.toString().trim()
            val imgBook = listBook[pos].bimg
            val author = binding.edtAuthor.text.toString().trim()
            val nxb = binding.edtNxb.text.toString().trim()
            val numPages = binding.edtNumpages.text.toString().trim()
            val kindOfSach = binding.edtKindOfSach.text.toString().trim()
            val priceText = binding.edtPrice.text.toString().trim()
            val price: Double = priceText.toDoubleOrNull() ?: 0.0
            val content = binding.edtMtContent.text.toString().trim()
            dialogProgress.show()
            dialogProgress.setTitle("Đang sửa !")
            if ((title.isEmpty() || title.isBlank()) || (author.isEmpty() || author.isBlank()) || (nxb.isEmpty() || nxb.isBlank())
                || (kindOfSach.isEmpty() || kindOfSach.isBlank()) || (numPages.isEmpty() || numPages.isBlank())
                || (priceText.isEmpty() || priceText.isBlank()) || (content.isEmpty() || content.isBlank())
            ) {
                dialogProgress.dismiss()
                if (title.isEmpty() || title.isBlank()) binding.edtTitle.error = "Nhập tiêu đề !"
                if (author.isEmpty() || author.isBlank()) binding.edtAuthor.error = "Nhập tác giả !"
                if (nxb.isEmpty() || nxb.isBlank()) binding.edtNxb.error = "Nhập nhà xuất bản !"
                if (kindOfSach.isEmpty() || kindOfSach.isBlank()) binding.edtKindOfSach.error = "Nhập loại !"
                if (numPages.isEmpty() || numPages.isBlank()) binding.edtNumpages.error = "Nhập số trang !"
                if (priceText.isEmpty() || priceText.isBlank()) binding.edtPrice.error = "Nhập giá !"
                if (content.isEmpty() || content.isBlank()) binding.edtMtContent.error = "Nhập nội dung !"
            }else{
                if (filePath == null) {
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        dialogProgress.dismiss()
                        val bookInfor = BookModel(id, title, imgBook, author, nxb, numPages, kindOfSach, price, content, null)
                        dbRef.child(id!!).setValue(bookInfor)
                        Toast.makeText(this, "Thành công !", Toast.LENGTH_SHORT).show()
                    },1700)
                }else{
                    filePath?.let {
                        storageRef.child(id!!).putFile(it)
                            .addOnSuccessListener {task ->
                                task.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { url ->
                                        dialogProgress.dismiss()
                                        val imgUrl = url.toString()
                                        val bookInforUrl = BookModel(id, title, imgUrl, author, nxb, numPages, kindOfSach, price, content, null)
                                        dbRef.child(id).setValue(bookInforUrl)
                                        Toast.makeText(this, "Thành công !", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        dialogProgress.dismiss()
                                        Toast.makeText(this, "Không thành công !", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
                }
            }
        }
    }

    private fun alertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)
        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang thêm !")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()
    }
    private fun btnChooseImg() {
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imgBookAdmin.setImageURI(it)
            if(it != null){
                filePath = it
            }
        }
        binding.btnChooseImg.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun btnAddBook() {
        binding.btnAddBook.setOnClickListener {
            addDataBook()
        }
    }

    private fun addDataBook() {
        val title = binding.edtTitle.text.toString().trim()
        val author = binding.edtAuthor.text.toString().trim()
        val nxb = binding.edtNxb.text.toString().trim()
        val numPages = binding.edtNumpages.text.toString().trim()
        val kindOfSach = binding.edtKindOfSach.text.toString().trim()
        val priceText = binding.edtPrice.text.toString().trim()
        val price: Double = priceText.toDoubleOrNull() ?: 0.0
        val content = binding.edtMtContent.text.toString().trim()
        dialogProgress.show()
        if ((title.isEmpty() || title.isBlank()) || (author.isEmpty() || author.isBlank()) || (nxb.isEmpty() || nxb.isBlank())
            || (kindOfSach.isEmpty() || kindOfSach.isBlank()) || (numPages.isEmpty() || numPages.isBlank())
            || (priceText.isEmpty() || priceText.isBlank()) || (content.isEmpty() || content.isBlank())
        ) {
            dialogProgress.dismiss()
            if (title.isEmpty() || title.isBlank()) binding.edtTitle.error = "Nhập tiêu đề !"
            if (author.isEmpty() || author.isBlank()) binding.edtAuthor.error = "Nhập tác giả !"
            if (nxb.isEmpty() || nxb.isBlank()) binding.edtNxb.error = "Nhập nhà xuất bản !"
            if (kindOfSach.isEmpty() || kindOfSach.isBlank()) binding.edtKindOfSach.error = "Nhập loại !"
            if (numPages.isEmpty() || numPages.isBlank()) binding.edtNumpages.error = "Nhập số trang !"
            if (priceText.isEmpty() || priceText.isBlank()) binding.edtPrice.error = "Nhập giá !"
            if (content.isEmpty() || content.isBlank()) binding.edtMtContent.error = "Nhập nội dung !"
        } else {
            val bookId = dbRef.push().key!!
            var bookModel: BookModel
            val handler = Handler(Looper.getMainLooper())
            filePath?.let {
                storageRef.child(bookId).putFile(it)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val imaUrl = url.toString()
                                bookModel = BookModel(bookId, title, imaUrl, author, nxb, numPages, kindOfSach, price, content, null)
                                dbRef.child(bookId).setValue(bookModel)
                                    .addOnCompleteListener {
                                        handler.postDelayed({
                                            dialogProgress.dismiss()
                                            binding.edtTitle.setText("")
                                            binding.edtAuthor.setText("")
                                            binding.edtNxb.setText("")
                                            binding.edtNumpages.setText("")
                                            binding.edtKindOfSach.setText("")
                                            binding.edtPrice.setText("")
                                            binding.edtMtContent.setText("")
                                            binding.imgBookAdmin.setImageDrawable(null)
                                            Toast.makeText(this, "Thành công !", Toast.LENGTH_SHORT).show()
                                        },200)
                                    }
                                    .addOnFailureListener {
                                        handler.postDelayed({
                                            dialogProgress.dismiss()
                                            Toast.makeText(this, "Thất bại ! ", Toast.LENGTH_SHORT).show()
                                        },200)
                                    }
                            }
                    }
            }
        }
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
            binding.edtKindOfSach.setText(listBook[pos].bkindOfSach.toString())
        }
    }
    private fun showButton(){
        if(listBook.size == 0){
            binding.btnUpdateBook.isEnabled = false
            binding.btnUpdateBook.visibility = View.GONE
        }else{
            binding.btnAddBook.isEnabled = false
            binding.btnAddBook.visibility = View.GONE
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