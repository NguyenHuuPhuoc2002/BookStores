package com.example.bookstores.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.bookstores.Model.LoginModel
import com.example.bookstores.databinding.FragmentRegisterBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fragmentManager = parentFragmentManager

        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase.reference.child("Account")

        val alertDialog = AlertDialog.Builder(context)
        val progressBar = ProgressBar(context)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng kí !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        binding.btnSigUp.setOnClickListener {
            dialog.show()
            val email = binding.edtEmailRegister.text.toString()
            val passWord = binding.edtPasswordRegister.text.toString()
            Register(email, passWord)
            view?.postDelayed({
                dialog.dismiss()
                Toast.makeText(context, "Đăng kí thành công!", Toast.LENGTH_SHORT).show()
                fragmentManager.popBackStack()
            }, 1500)

        }
        binding.txtback.setOnClickListener {
            fragmentManager.popBackStack()
        }
        return binding.root
    }

    private fun Register(email: String, password: String) {
        dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val edtEmail = binding.edtEmailRegister
                    val edtPassWord = binding.edtPasswordRegister
                    val edtReEnterPassWord = binding.edtReEnterPassword

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
                    } else{

                        val id = dbRef.push().key
                        val logIn = LoginModel(id, email, password)
                        dbRef.child(id!!).setValue(logIn)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }
}
