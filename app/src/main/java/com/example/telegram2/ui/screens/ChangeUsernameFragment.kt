package com.example.telegram2.ui.screens

import android.os.Bundle
import android.view.*
import com.example.telegram2.R
import com.example.telegram2.database.*

import com.example.telegram2.databinding.FragmentChangeUsernameBinding
import com.example.telegram2.utillits.*
import java.util.*


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {
    lateinit var bindingClass: FragmentChangeUsernameBinding
    lateinit var mNewUsername:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass= FragmentChangeUsernameBinding.inflate(inflater)
        return bindingClass.root
    }

    override fun onResume() {
        super.onResume()
        bindingClass.settingsInputUsername.setText(USER.username)

    }


    override fun change() {
        mNewUsername= bindingClass.settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
        if(mNewUsername.isEmpty()){
            showToast("Поле Пустое")
        } else{
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener{
                    if (it.hasChild(mNewUsername))
                    {
                        showToast("Такой пользователь уже существует")}
                    else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    updateCurrentUsername(mNewUsername)
                }
            }
    }
}