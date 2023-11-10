package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.bookstores.Activity.DetailActivity
import com.example.bookstores.Activity.MainActivity
import com.example.bookstores.R
import java.lang.ref.WeakReference
import java.nio.file.attribute.AclEntry.Builder

class SuccessfulOrderFragment : Fragment() {
    private lateinit var mView:View
    private lateinit var activity: WeakReference<DetailActivity>
    private lateinit var fragmentManager: FragmentManager
    private lateinit var dialogProgress: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_successful_oder, container, false)
        activity = WeakReference(requireActivity() as DetailActivity)
        fragmentManager = parentFragmentManager

        val alertDialog = AlertDialog.Builder(requireActivity())
        val processBar = ProgressBar(requireActivity())

        alertDialog.setView(processBar)
        alertDialog.setTitle("Đang gửi đánh giá")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()


        val activity = activity.get()
        mView.findViewById<RatingBar>(R.id.ratingBar).setOnRatingBarChangeListener { rBar, fl, b ->
            mView.findViewById<TextView>(R.id.txtinfo).text = fl.toString()
            when(rBar.rating.toInt()){
                1 -> mView.findViewById<TextView>(R.id.txtinfo).text = "Rất tệ"
                2 -> mView.findViewById<TextView>(R.id.txtinfo).text = "Tệ"
                3 -> mView.findViewById<TextView>(R.id.txtinfo).text = "Tốt"
                4 -> mView.findViewById<TextView>(R.id.txtinfo).text = "Tuyệt"
                5 -> mView.findViewById<TextView>(R.id.txtinfo).text = "Tuyệt vời"
                else -> mView.findViewById<TextView>(R.id.txtinfo).text = ""
            }
        }
        mView.findViewById<ImageView>(R.id.imgback).setOnClickListener {
            activity?.binding?.imgback?.isEnabled = true
            activity?.binding?.imgaddcart?.isEnabled = true
            activity?.binding?.btnbuy?.isEnabled = true
            activity?.binding?.imgNav?.isEnabled = true
            fragmentManager.popBackStack()
        }

        mView.findViewById<Button>(R.id.btnEvaluate).setOnClickListener {
            dialogProgress.show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                activity?.binding?.imgback?.isEnabled = true
                activity?.binding?.imgaddcart?.isEnabled = true
                activity?.binding?.btnbuy?.isEnabled = true
                activity?.binding?.imgNav?.isEnabled = true
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }, 1200)
        }
        return mView
    }

}