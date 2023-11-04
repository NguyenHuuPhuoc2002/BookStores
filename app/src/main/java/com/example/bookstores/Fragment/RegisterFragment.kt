package com.example.bookstores.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.bookstores.Activity.LoginActivity
import com.example.bookstores.R
import com.example.bookstores.interfaces.Model.LoginModel
import com.example.bookstores.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.WeakReference

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var dialog: Dialog
    private lateinit var activityRef: WeakReference<LoginActivity>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fragmentManager = parentFragmentManager
        activityRef = WeakReference(requireActivity() as LoginActivity)
        firebaseAuth = FirebaseAuth.getInstance()

        val alertDialog = AlertDialog.Builder(context)
        val progressBar = ProgressBar(context)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng kí !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        //lấy view từ MainActivity
        val activity = activityRef.get()
        val txtRegister = activity?.txtRegister()
        val btnLogin = activity?.btnLogin()
        val txtForgotPass = activity?.txtFogotPass()

        binding.btnSigUp.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            Register(email, password)
            if (activity != null) {
                txtRegister?.isEnabled = true
                btnLogin?.isEnabled = true
                txtForgotPass?.isEnabled = true
            }
        }
        binding.txtback.setOnClickListener {
            if (activity != null) {
                txtRegister?.isEnabled = true
                btnLogin?.isEnabled = true
                txtForgotPass?.isEnabled = true
            }
            fragmentManager.popBackStack()
        }


        return binding.root
    }
    private fun Register(email: String, password: String) {
        val edtEmail = binding.edtEmailRegister
        val edtPassWord = binding.edtPasswordRegister
        val edtReEnterPassWord = binding.edtReEnterPassword
        dialog.show()
        if (edtEmail.text?.isEmpty() == true || edtPassWord.text?.isEmpty() == true
            || edtReEnterPassWord.text?.isEmpty() == true
            || !edtReEnterPassWord.text?.toString()?.trim()?.equals(edtPassWord.text.toString().trim())!!
        ) {
            if (edtEmail.text?.isEmpty() == true) {
                edtEmail.error = "Vui lòng nhập địa chỉ email"
            }
            if (edtPassWord.text?.isEmpty() == true) {
                edtPassWord.error = "Vui lòng nhập mật khẩu"
            }
            if (edtReEnterPassWord.text?.isEmpty() == true) {
                edtReEnterPassWord.error = "Vui lòng nhập lại mật khẩu"
            }
            if (!edtReEnterPassWord.text?.toString()?.trim()?.equals(edtPassWord.text?.toString()?.trim())!!) {
                edtReEnterPassWord.error = "Mật khẩu không khớp"
            }
            dialog.dismiss()
        } else{
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        view?.postDelayed({
                            dialog.dismiss()
                            Toast.makeText(context, "Đăng kí thành công!", Toast.LENGTH_SHORT).show()
                            fragmentManager.popBackStack()
                        }, 1500)
                    } else {
                        dialog.dismiss()
                        val errorMessage = task.exception?.message
                        Toast.makeText(context, "Lỗi: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

