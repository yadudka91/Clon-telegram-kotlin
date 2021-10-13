package com.example.telegram2.ui.screens

import android.os.Bundle
import android.view.*
import com.example.telegram2.R
import com.example.telegram2.database.USER
import com.example.telegram2.database.setNameToDatabase
import com.example.telegram2.databinding.FragmentChangeNameBinding
import com.example.telegram2.utillits.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    lateinit var bindingClass: FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentChangeNameBinding.inflate(inflater)
        return bindingClass.root
    }


    override fun onResume() {
        super.onResume()

        initFullnameList()


    }

    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            bindingClass.settingsInputName.setText(fullnameList[0])
            bindingClass.settingsInputSurname.setText(fullnameList[1])
        } else {
            bindingClass.settingsInputName.setText(fullnameList[0])
        }
    }

    override fun change() {
        val name = bindingClass.settingsInputName.text.toString()
        val surname = bindingClass.settingsInputSurname.text.toString()
        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else{
            val fullname = "$name $surname"
            setNameToDatabase(fullname)

        }

    }


}