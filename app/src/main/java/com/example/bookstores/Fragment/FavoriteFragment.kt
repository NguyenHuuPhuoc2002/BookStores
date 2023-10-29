package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstores.Activity.DetailActivity
import com.example.bookstores.Activity.Adapter.RvAdapterFavourite
import com.example.bookstores.Activity.MainActivity
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList

class FavoriteFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var dbRef: DatabaseReference
    private lateinit var mList: ArrayList<BookModel>
    private lateinit var mAdapter: RvAdapterFavourite
    private lateinit var dialogProgress: Dialog
    private lateinit var activityRef: WeakReference<MainActivity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_favourite, container, false)
        mList = arrayListOf<BookModel>()

        val alertDialog = AlertDialog.Builder(context)
        val progressBar = ProgressBar(context)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang Xóa !")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()

        getSach()
        clearAll()
        return mView
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun clearAll(){
        activityRef = WeakReference(requireActivity() as MainActivity)
        val activity = activityRef.get()
        if (activity != null) {
            val txtClear = activity.txtClearFavourite()
            txtClear.setOnClickListener {
                if(mList.size >= 1){
                    val alertDialogBuilder = AlertDialog.Builder(requireActivity())
                    alertDialogBuilder.setTitle("Xác nhận xóa")
                    alertDialogBuilder.setMessage("Bạn có muốn xóa hết không?")

                    alertDialogBuilder.setPositiveButton("Có") { dialog: DialogInterface, _: Int ->
                        // Xử lý khi người dùng chọn "Có"
                        val alertDialog = AlertDialog.Builder(requireActivity())
                        val progressBar = ProgressBar(requireActivity())

                        alertDialog.setView(progressBar)
                        alertDialog.setTitle("Đang xóa !")
                        alertDialog.setCancelable(false)
                        dialogProgress = alertDialog.create()
                        dialogProgress.show()

                        val handler = android.os.Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            dbRef.removeValue()
                            mList.clear()
                            mAdapter.notifyDataSetChanged()
                            Toast.makeText(requireActivity(), "Xóa thành công !", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            dialogProgress.dismiss()
                        }, 1000)
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
        }
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
                }
                mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}