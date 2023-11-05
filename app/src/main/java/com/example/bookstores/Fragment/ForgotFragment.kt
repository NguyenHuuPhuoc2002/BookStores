package com.example.bookstores.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.bookstores.Activity.LoginActivity
import com.example.bookstores.Activity.MainActivity
import com.example.bookstores.R
import com.example.bookstores.databinding.FragmentForgotBinding
import com.example.bookstores.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import java.lang.ref.WeakReference

class ForgotFragment : Fragment() {

    private lateinit var binding: FragmentForgotBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var activityRef: WeakReference<LoginActivity>
    private lateinit var dialog: Dialog
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotBinding.inflate(inflater, container, false)
        fragmentManager = parentFragmentManager
        activityRef = WeakReference(requireActivity() as LoginActivity)

        firebaseAuth = FirebaseAuth.getInstance()
        val alertDialog = AlertDialog.Builder(requireActivity())
        val progressBar = ProgressBar(requireActivity())

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang thực hiện")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        binding.btnReset.setOnClickListener {
            dialog.show()
            if (binding.edtEmailForgot.text?.isEmpty() == true ) {
                dialog.dismiss()
                if (binding.edtEmailForgot.text?.isEmpty() == true) {
                    binding.edtEmailForgot.error = "Vui lòng nhập địa chỉ email"
                }
            } else{
                val sPassword = binding.edtEmailForgot.text.toString()
                firebaseAuth.sendPasswordResetEmail(sPassword)
                    .addOnSuccessListener {
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            dialog.dismiss()
                            Toast.makeText(requireActivity(), "Vui lòng kiểm tra email của bạn!", Toast.LENGTH_SHORT).show()
                            binding.edtEmailForgot.setText("")
                        },1200)
                    }
                    .addOnFailureListener {
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            dialog.dismiss()
                            Toast.makeText(requireActivity(), "Email không tồn tại!", Toast.LENGTH_SHORT).show()
                        },1200)
                    }
            }
        }


        binding.txtback.setOnClickListener {
            //lấy view từ MainActivity
            val activity = activityRef.get()
            if (activity != null) {
                activity.binding.txtregister.isEnabled = true
                activity.binding.btnlogin.isEnabled = true
                activity.binding.txtForgetPass.isEnabled = true
            }
            fragmentManager.popBackStack()

        }
        return binding.root
    }


}