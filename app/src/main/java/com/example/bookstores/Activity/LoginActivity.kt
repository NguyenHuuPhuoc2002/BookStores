package com.example.bookstores.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bookstores.Fragment.ForgotFragment
import com.example.bookstores.Fragment.RegisterFragment
import com.example.bookstores.interfaces.Model.LoginModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var mList: ArrayList<LoginModel>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        fragmentManager = supportFragmentManager
        mList = arrayListOf<LoginModel>()

        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng nhập !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        binding.txtregister.setOnClickListener {
            binding.btnlogin.isEnabled = false
            binding.txtForgetPass.isEnabled = false
            binding.txtregister.isEnabled = false
            openFragment(RegisterFragment())
        }
        binding.btnlogin.setOnClickListener {
            val edtEmail = binding.edtemail.text.toString()
            val edtPassWord = binding.edtpassword.text.toString()
            if (edtEmail.isEmpty() || edtPassWord.isEmpty()) {
                dialog.dismiss()
                if (binding.edtemail.text?.isEmpty() == true) {
                    binding.edtemail.error = "Vui lòng nhập địa chỉ email !"
                }
                if ( binding.edtpassword.text?.isEmpty() == true) {
                    binding.edtpassword.error = "Vui lòng nhập mật khẩu ! "
                }
            }else {
                dialog.show()
                logIn(edtEmail, edtPassWord)
            }
        }
        binding.txtForgetPass.setOnClickListener {
            binding.txtregister.isEnabled = false
            binding.btnlogin.isEnabled = false
            binding.txtForgetPass.isEnabled = false
            forgotPass()
        }

        //checkbox remember password
        val preferences: SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE)
        val checkbox: String? = preferences.getString("remember", "")
        if (checkbox == "true") {
            val email: String? = preferences.getString("email", "")
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("Login", "Đăng nhập thành công !")
            intent.putExtra("email", email?.replace(".", ""))
            intent.putExtra("emailAcountTitle", email)
            startActivity(intent)
        } else if (checkbox == "false") {
        }

        binding.cbRember.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                val edtEmail = binding.edtemail.text.toString()
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember", "true")
                editor.putString("email", edtEmail)
                editor.apply()
            } else if (!isChecked) {
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.putString("remember", "false")
                editor.remove("email")
                editor.apply()
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }
    private fun forgotPass(){
        openFragment(ForgotFragment())
    }
    private fun logIn(email: String, password: String) {
       firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
           if (task.isSuccessful) {
               val intent = Intent(this@LoginActivity, MainActivity::class.java)
               intent.putExtra("Login", "Đăng nhập thành công !")
               intent.putExtra("email", email.replace(".", ""))
               intent.putExtra("emailAcountTitle", email)
               startActivity(intent)
               finish()
           } else {
               dialog.dismiss()
               Toast.makeText(this, "Đăng nhập không thành công !", Toast.LENGTH_SHORT).show()
           }
       }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.endter_from_right, R.anim.exit_to_right, R.anim.endter_from_right, R.anim.exit_to_right)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
