package com.example.telegram2.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram2.R
import com.example.telegram2.database.CURRENT_UID
import com.example.telegram2.ui.message_recycler_view.views.MessageView
import com.example.telegram2.utillits.asTime

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blocUserMessage = view.findViewById<ConstraintLayout>(R.id.block_user_message)
    private val chatUserMessage = view.findViewById<TextView>(R.id.chat_user_message)
    private val chatUserMessageTime = view.findViewById<TextView>(R.id.chat_user_message_time)

    private val blocReceivedMessage =
        view.findViewById<ConstraintLayout>(R.id.block_received_message)
    private val chatReceivedMessage = view.findViewById<TextView>(R.id.chat_received_message)
    private val chatReceivedMessegeTime =
        view.findViewById<TextView>(R.id.chat_received_message_time)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserMessage.visibility = View.VISIBLE
            blocReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.asTime()
        } else {
            blocUserMessage.visibility = View.GONE
            blocReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessegeTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDettach() {
    }
}