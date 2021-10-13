package com.example.telegram2.ui.screens.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram2.R
import com.example.telegram2.database.*

import com.example.telegram2.databinding.FragmentEnterCodeBinding
import com.example.telegram2.utillits.*
import com.google.firebase.auth.PhoneAuthProvider


class EnterCodeFragment(val mPhoneNumber: String, val id: String) : Fragment(R.layout.fragment_enter_code) {
    lateinit var bindingClass: FragmentEnterCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentEnterCodeBinding.inflate(inflater)
        return bindingClass.root
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = mPhoneNumber
        bindingClass.registerInputCode.addTextChangedListener(AppTextWatcher{
                val string = bindingClass.registerInputCode.text.toString()
                if (string.length==6){
                    enterCode()
                }
        })
    }
    private fun enterCode(){
        val code = bindingClass.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task->
            if (task.isSuccessful){
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = mPhoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).addListenerForSingleValueEvent(
                    AppValueEventListener{
                        if (!it.hasChild(CHILD_USERNAME)){
                            dateMap[CHILD_USERNAME] = uid
                        }

                        REF_DATABASE_ROOT.child(NODE_PHONES).child(mPhoneNumber).setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener { REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                .addOnSuccessListener {
                                    showToast("Добро пожаловать")
                                    restartActivity()
                                }.addOnFailureListener { showToast(it.message.toString()) }
                            }
                    }
                )
            } else
                showToast(task.exception?.message.toString())
        }
    }
}