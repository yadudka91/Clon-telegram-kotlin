package com.example.telegram2.ui.message_recycler_view.view_holders

import android.view.View
import com.example.telegram2.ui.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view:MessageView)
    fun onAttach(view: MessageView)
    fun onDettach()
}