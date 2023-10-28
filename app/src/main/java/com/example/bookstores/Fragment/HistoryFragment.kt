package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Activity.Adapter.RvAdapterHistory
import com.example.bookstores.interfaces.Model.BookHistoryModel
import com.example.bookstores.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.WeakReference

class HistoryFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mList: ArrayList<BookHistoryModel>
     lateinit var mAdapter: RvAdapterHistory
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_history, container, false)
        mList = arrayListOf<BookHistoryModel>()
        getSach()
        return mView
    }

    private fun getSach() {
        mView.findViewById<RecyclerView>(R.id.rcv_history).visibility = View.GONE
        mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("BookHistory")
        dbRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("CutPasteId")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                if(snapshot.exists()){
                    for(book in snapshot.children){
                        val bookData = book.getValue(BookHistoryModel::class.java)
                        if (bookData != null) {
                            mList.add(bookData)
                        }
                    }
                    mAdapter = RvAdapterHistory(mList,  WeakReference(this@HistoryFragment))
                    mView.findViewById<RecyclerView>(R.id.rcv_history).visibility = View.VISIBLE
                    mView.findViewById<RecyclerView>(R.id.rcv_history).adapter = mAdapter
                    mView.findViewById<RecyclerView>(R.id.rcv_history).layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
                    mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}