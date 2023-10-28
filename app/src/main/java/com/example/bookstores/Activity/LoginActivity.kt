package com.example.bookstores.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bookstores.Fragment.RegisterFragment
import com.example.bookstores.Model.LoginModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityLoginBinding
import com.example.bookstores.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    private lateinit var mList: ArrayList<LoginModel>
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Account")

        fragmentManager = supportFragmentManager
        mList = arrayListOf<LoginModel>()

        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng nhập !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        binding.txtregister.setOnClickListener {
            openFragment(RegisterFragment())
        }
        binding.btnlogin.setOnClickListener {
            val edtEmail = binding.edtemail.text.toString()
            val edtPassWord = binding.edtpassword.text.toString()
            if (edtEmail.isEmpty() || edtPassWord.isEmpty()) {
                dialog.dismiss()
                if (binding.edtemail.text?.isEmpty() == true) {
                    binding.edtemail.error = "Vui lòng nhập địa chỉ email"
                }
                if ( binding.edtpassword.text?.isEmpty() == true) {
                    binding.edtpassword.error = "Vui lòng nhập mật khẩu"
                }
            }else {
                dialog.show()
                logIn(edtEmail, edtPassWord)
            }
        }
        binding.txtForgetPass.setOnClickListener {
            forgetPass()
        }
    }

    private fun forgetPass(){
        Toast.makeText(this, "Quên mật khẩu à !", Toast.LENGTH_SHORT).show()
    }
    private fun logIn(email: String, password: String) {
        dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailMatched = false // Biến kiểm tra email có tồn tại

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val account = data.getValue(LoginModel::class.java)
                        if (account != null) {
                            emailMatched = true // Đánh dấu rằng email đúng
                            if (account.passWord == password) {
                                // Mật khẩu đúng cho email tìm thấy
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("Login", "Đăng nhập thành công !")
                                intent.putExtra("email", account.email)
                                startActivity(intent)
                                finish()
                                return
                            } else {
                                dialog.dismiss()
                                binding.edtpassword.error = "Sai mật khẩu"
                            }
                        }
                    }
                }
                if (!emailMatched) {
                    dialog.dismiss()
                    binding.edtemail.error = "Email không tồn tại" // Email không tồn tại
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }




    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
}
