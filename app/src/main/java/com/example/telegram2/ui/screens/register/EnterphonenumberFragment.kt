package com.example.telegram2.ui.screens.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram2.R
import com.example.telegram2.database.AUTH

import com.example.telegram2.databinding.FragmentEnterphonenumberBinding
import com.example.telegram2.utillits.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class EnterphonenumberFragment : Fragment(R.layout.fragment_enterphonenumber) {
    lateinit var bindingClass: FragmentEnterphonenumberBinding
    lateinit var mPhoneNumber: String
    lateinit var mCallback:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentEnterphonenumberBinding.inflate(inflater)
        return bindingClass.root
    }

    override fun onStart() {
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            // келбек возвращает результат верефикации
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //функция срабатывает если верификация уже была произведена
                // пользователь авторизуется в приложение без подтверджение по смс
                AUTH.signInWithCredential(credential).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        showToast("Добро пожаловать")
                    restartActivity()
                    } else
                        showToast(task.exception?.message.toString())

                }
            }
            override fun onVerificationFailed(p0: FirebaseException) {
                //функция срабатывает если верификация не удалась
             showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))

            }
        }
        super.onStart()
        bindingClass.registerBtnNext.setOnClickListener {
            sendcode()
        }


    }

    private fun sendcode() {
        if (bindingClass.registerInputPhoneNumber.text.toString().isEmpty()){
            showToast(getString(R.string.register_toast_enter_phone))
        } else{
            authUser()
        }
    }

    private fun authUser() {
        // инициализация
        mPhoneNumber=bindingClass.registerInputPhoneNumber.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
            APP_ACTIVITY,
            mCallback
        )

    }
}