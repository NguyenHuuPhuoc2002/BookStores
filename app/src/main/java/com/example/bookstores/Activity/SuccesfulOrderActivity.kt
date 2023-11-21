package com.example.bookstores.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.bookstores.R

class SuccesfulOrderActivity : AppCompatActivity() {

    private lateinit var dialogProgress: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_succesful_order)
        val alertDialog = AlertDialog.Builder(this)
        val processBar = ProgressBar(this)

        alertDialog.setView(processBar)
        alertDialog.setTitle("Đang gửi đánh giá !")
        alertDialog.setCancelable(false)
        dialogProgress = alertDialog.create()

        findViewById<ImageView>(R.id.imgback).setOnClickListener {
            dialogProgress.show()
            dialogProgress.setTitle("Chờ giây lát !")
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                finish()
            }, 500)
        }

        findViewById<RatingBar>(R.id.ratingBar).setOnRatingBarChangeListener { rBar, fl, b ->
            findViewById<TextView>(R.id.txtinfo).text = fl.toString()
            when(rBar.rating.toInt()){
                1 -> findViewById<TextView>(R.id.txtinfo).text = "Rất tệ"
                2 -> findViewById<TextView>(R.id.txtinfo).text = "Tệ"
                3 -> findViewById<TextView>(R.id.txtinfo).text = "Tốt"
                4 -> findViewById<TextView>(R.id.txtinfo).text = "Tuyệt"
                5 -> findViewById<TextView>(R.id.txtinfo).text = "Tuyệt vời"
                else -> findViewById<TextView>(R.id.txtinfo).text = ""
            }
        }

        findViewById<Button>(R.id.btnEvaluate).setOnClickListener {
            dialogProgress.show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                finish()
            }, 1200)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        dialogProgress.dismiss()
    }
}