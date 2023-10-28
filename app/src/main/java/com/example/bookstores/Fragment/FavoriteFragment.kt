package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Activity.DetailActivity
import com.example.bookstores.Activity.Adapter.RvAdapterFavourite
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class FavoriteFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var dbRef: DatabaseReference
    private lateinit var mList: ArrayList<BookModel>
    private lateinit var mAdapter: RvAdapterFavourite
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_favourite, container, false)
        mList = arrayListOf<BookModel>()
        getSach()
        return mView
    }

    private fun getSach() {
        mView.findViewById<RecyclerView>(R.id.rcvfavourite).visibility = View.GONE
        mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("BookFavourite")
        dbRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("CutPasteId")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                if(snapshot.exists()){
                    for(book in snapshot.children){
                        val bookData = book.getValue(BookModel::class.java)
                        if (bookData != null) {
                            mList.add(bookData)
                        }
                    }
                    mAdapter = RvAdapterFavourite(mList)
                    mView.findViewById<RecyclerView>(R.id.rcvfavourite).visibility = View.VISIBLE
                    mView.findViewById<RecyclerView>(R.id.rcvfavourite).adapter = mAdapter
                    mView.findViewById<RecyclerView>(R.id.rcvfavourite).layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                    mAdapter.setOnItemClickListener(object : onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(context, DetailActivity::class.java )
                            val bundle = Bundle()
                            val bookList = ArrayList<Parcelable>(mList)
                            bundle.putParcelableArrayList("bookList", bookList)
                            bundle.putInt("pos", position)
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    })
                    mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}