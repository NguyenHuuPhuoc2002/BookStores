package com.example.bookstores.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Adapter.RvAdapterAdmin
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityAdminBinding
import com.example.bookstores.databinding.ActivityLoginBinding
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.Normalizer

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var mAdapter: RvAdapterAdmin
    private lateinit var dbRef: DatabaseReference
    private lateinit var listBook: ArrayList<BookModel>
    private lateinit var filteredListBook: ArrayList<BookModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbRef = FirebaseDatabase.getInstance().getReference("BookHome")
        listBook = arrayListOf()


        showKeyboard()
        getBook()
        searchView()
    }

    private fun getBook() {
        dbRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("CutPasteId")
            override fun onDataChange(snapshot: DataSnapshot) {
                listBook.clear()
                if(snapshot.exists()){
                    for(book in snapshot.children){
                        val bookData = book.getValue(BookModel::class.java)
                        if(bookData != null){
                            listBook.add(bookData)
                        }
                    }
                    filteredListBook = ArrayList(listBook)
                    mAdapter = RvAdapterAdmin(listBook)
                    binding.rcvBook.adapter = mAdapter
                    binding.rcvBook.layoutManager = GridLayoutManager(this@AdminActivity, 2, GridLayoutManager.VERTICAL, false)
                    mAdapter.setOnItemClickListener(object : onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@AdminActivity, AdminDetailActivity::class.java)
                            startActivity(intent)
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun searchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtererList(newText)
                return true
            }
        })
    }

    private fun filtererList(query: String?) {
        if (query != null) {
            //chuyển hóa chuỗi loại bỏ dấu để so sánh
            val normalizedQuery = removeDiacritics(query.toLowerCase())
            filteredListBook = ArrayList()
            for (i in listBook) {
                val normalizedTitle = i.btitle?.let { removeDiacritics(it.toLowerCase()) }
                if (normalizedTitle != null) {
                    if (normalizedTitle.contains(normalizedQuery)) {
                        filteredListBook.add(i)
                    }
                }
            }
            mAdapter.clearData()
            mAdapter.setFilteredList(filteredListBook)

        }
    }

    private fun removeDiacritics(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
    }
    private fun showKeyboard() {
        binding.searchView.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(findViewById(R.id.search_view), InputMethodManager.SHOW_IMPLICIT)
        }
    }

}