package com.example.bookstores.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Activity.Adapter.RvAdapter
import com.example.bookstores.Adapter.RvAdapterAdmin
import com.example.bookstores.Fragment.HomeFragment
import com.example.bookstores.Model.UserModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityAdminBinding
import com.example.bookstores.databinding.ActivityLoginBinding
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.interfaces.onItemClickListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.WeakReference
import java.text.Normalizer

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var mAdapter: RvAdapterAdmin
    private lateinit var dbRef: DatabaseReference
    lateinit var emailAcountTitle: String
    private lateinit var dialog: Dialog
    private lateinit var listBook: ArrayList<BookModel>
    private lateinit var filteredListBook: ArrayList<BookModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbRef = FirebaseDatabase.getInstance().getReference("BookHome")
        listBook = arrayListOf()
        mAdapter = RvAdapterAdmin(listBook)

        navHeadEmailUser()
        alertDialog()
        showKeyboard()
        getBooks()
        searchView()
        btnimgNavigation()
        Navigation()
        btnAdd()
    }

    private fun btnAdd() {
        binding.imgAddBook.setOnClickListener {
            startActivity(Intent(this@AdminActivity, AdminDetailActivity::class.java))
        }
    }

    private fun navHeadEmailUser() {
        val intent = intent
        emailAcountTitle = intent.getStringExtra("emailAcountTitle").toString()
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)
        val headerView = navigationView.getHeaderView(0)
        val emailTextView = headerView.findViewById<TextView>(R.id.txtemail)
        emailTextView.text = emailAcountTitle
    }
    private fun alertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)
        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng xuất !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()
    }
    private fun btnimgNavigation(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.draw_layout)
        val navView = findViewById<NavigationView>(R.id.navigation_drawer)
        binding.imgNav.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }
    private fun Navigation(){
        findViewById<NavigationView>(R.id.navigation_drawer).setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                   finish()
                }
                R.id.nav_aboutapp -> Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
                R.id.nav_out -> {
                    dialog.show()
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        val preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = preferences.edit()
                        editor.putString("remember", "false")
                        editor.apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }, 1200)
                }
            }
            binding.drawLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun getBooks() {
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
                    val reversedList = listBook.reversed()
                    filteredListBook = ArrayList(listBook)
                    mAdapter = RvAdapterAdmin(reversedList)
                    binding.rcvBook.adapter = mAdapter
                    binding.rcvBook.layoutManager = GridLayoutManager(this@AdminActivity, 2, GridLayoutManager.VERTICAL, false)
                    mAdapter.setOnItemClickListener(object : onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val clickedBook = reversedList[position]
                            val originalPosition = listBook.indexOf(clickedBook)
                            val intent = Intent(this@AdminActivity, AdminDetailActivity::class.java )
                            val bundle = Bundle()
                            val bookList = ArrayList<Parcelable>(listBook)
                            bundle.putParcelableArrayList("bookList", bookList)
                            bundle.putInt("pos", originalPosition)
                            intent.putExtras(bundle)
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