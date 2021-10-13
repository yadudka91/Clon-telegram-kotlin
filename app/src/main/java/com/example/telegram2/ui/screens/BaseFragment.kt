package com.example.telegram2.ui.screens

import androidx.fragment.app.Fragment
import com.example.telegram2.utillits.APP_ACTIVITY


open class BaseFragment (layout: Int): Fragment(layout) {


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.myAppDrawer.disableDrawer()
    }

}