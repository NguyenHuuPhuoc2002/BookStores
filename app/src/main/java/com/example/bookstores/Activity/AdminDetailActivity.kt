package com.example.bookstores.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityAdminBinding
import com.example.bookstores.databinding.ActivityAdminDetailBinding

class AdminDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}