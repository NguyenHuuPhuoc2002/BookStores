package com.example.bookstores.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.bookstores.Activity.LoginActivity
import com.example.bookstores.Model.UserModel
import com.example.bookstores.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var dialog: Dialog
    private lateinit var activityRef: WeakReference<LoginActivity>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fragmentManager = parentFragmentManager
        activityRef = WeakReference(requireActivity() as LoginActivity)
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val alertDialog = AlertDialog.Builder(context)
        val progressBar = ProgressBar(context)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng kí !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        //lấy view từ MainActivity
        val activity = activityRef.get()

        binding.btnSigUp.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            Register(email, password)
            if (activity != null) {
                activity.binding.txtregister.isEnabled = true
                activity.binding.btnlogin.isEnabled = true
                activity.binding.txtForgetPass.isEnabled = true
            }
        }
        binding.txtback.setOnClickListener {
            if (activity != null) {
                activity.binding.txtregister.isEnabled = true
                activity.binding.btnlogin.isEnabled = true
                activity.binding.txtForgetPass.isEnabled = true
            }
            fragmentManager.popBackStack()
        }
        val callback = object : OnBackPressedCallback(true ) {
            override fun handleOnBackPressed() {
                if (activity != null) {
                    activity.binding.txtregister.isEnabled = true
                    activity.binding.btnlogin.isEnabled = true
                    activity.binding.txtForgetPass.isEnabled = true
                }
                fragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }
    private fun Register(email: String, password: String) {
        val edtEmail = binding.edtEmailRegister
        val edtPassWord = binding.edtPasswordRegister
        val edtReEnterPassWord = binding.edtReEnterPassword
        val edtName = binding.edtNameRegister
        val edtSdt = binding.edtPhoneNumRegister
        dialog.show()
        if ((edtEmail.text?.isEmpty() == true && edtEmail.text?.isBlank() == true)
            || (edtPassWord.text?.isEmpty() == true && edtPassWord.text?.isBlank() == true)
            || (edtReEnterPassWord.text?.isEmpty() == true && edtReEnterPassWord.text?.isBlank() == true)
            || (edtName.text?.isEmpty() == true && edtName.text?.isBlank() == true)
            || (edtSdt.text?.isEmpty() == true && edtSdt.text?.isBlank() == true)
            || (!edtReEnterPassWord.text?.toString()?.trim()?.equals(edtPassWord.text.toString().trim())!!)
        ) {
            if (edtEmail.text?.isEmpty() == true) {
                edtEmail.error = "Vui lòng nhập địa chỉ email!"
            }
            if (edtPassWord.text?.isEmpty() == true) {
                edtPassWord.error = "Vui lòng nhập mật khẩu!"
            }
            if (edtReEnterPassWord.text?.isEmpty() == true) {
                edtReEnterPassWord.error = "Vui lòng nhập lại mật khẩu!"
            }
            if (edtName.text?.isEmpty() == true) {
                edtName.error = "Vui lòng nhập họ tên!"
            }
            if (edtSdt.text?.isEmpty() == true) {
                edtSdt.error = "Vui lòng nhập số điện thoại!"
            }
            if (!edtReEnterPassWord.text?.toString()?.trim()?.equals(edtPassWord.text?.toString()?.trim())!!) {
                edtReEnterPassWord.error = "Mật khẩu không khớp!"
            }
            dialog.dismiss()
        } else{
            val id = dbRef.push().key
            val hoTen = edtName.text.toString()
            val sDT = edtSdt.text.toString()
            val email = edtEmail.text.toString()
            val passWord = edtPassWord.text.toString()
            val reEnterPassWord = edtReEnterPassWord.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        view?.postDelayed({
                            dialog.dismiss()
                            val user = UserModel(id, hoTen, sDT, email,0)
                            dbRef.child(id!!).setValue(user)
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

