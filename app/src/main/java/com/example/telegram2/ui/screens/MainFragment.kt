package com.example.telegram2.ui.screens

import androidx.fragment.app.Fragment
import com.example.telegram2.R
import com.example.telegram2.utillits.APP_ACTIVITY
import com.example.telegram2.utillits.hideKeyboard


class MainFragment : Fragment(R.layout.fragment_chats) {



    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
                }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.myAppDrawer.enableDrawer() // Drawer включить
        hideKeyboard()
    }
}


