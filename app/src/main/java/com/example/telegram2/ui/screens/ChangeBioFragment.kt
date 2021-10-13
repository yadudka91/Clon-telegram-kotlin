package com.example.telegram2.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram2.R
import com.example.telegram2.database.USER
import com.example.telegram2.database.setBioToDatabase
import com.example.telegram2.databinding.FragmentChangeBioBinding
import com.example.telegram2.utillits.*


class ChangeBioFragment() : BaseChangeFragment(R.layout.fragment_change_bio) {
    lateinit var bindingClass: FragmentChangeBioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentChangeBioBinding.inflate(inflater)
        return bindingClass.root
    }


    override fun onResume() {
        super.onResume()
        bindingClass.settingsInputBio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = bindingClass.settingsInputBio.text.toString()
        setBioToDatabase(newBio)


    }


}