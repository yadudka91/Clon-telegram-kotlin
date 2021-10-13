package com.example.telegram2.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram2.R
import com.example.telegram2.database.CURRENT_UID
import com.example.telegram2.ui.message_recycler_view.views.MessageView
import com.example.telegram2.utillits.asTime
import com.example.telegram2.utillits.downloadAndSetImage

class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blocReceivedImageMessage =
        view.findViewById<ConstraintLayout>(R.id.block_received_image_message)
    private val chatReceivedImage = view.findViewById<ImageView>(R.id.chat_received_image)
    private val chatReceivedImageMessageTime =
        view.findViewById<TextView>(R.id.chat_received_image_message_time)

    private val blocUserImageMessage =
        view.findViewById<ConstraintLayout>(R.id.block_user_image_message)
    private val chatUserImage = view.findViewById<ImageView>(R.id.chat_user_image)
    private val chatUserImageMessageTime =
        view.findViewById<TextView>(R.id.chat_user_image_message_time)


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocReceivedImageMessage.visibility = View.GONE
            blocUserImageMessage.visibility = View.VISIBLE
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view.timeStamp.asTime()
        } else {
            blocUserImageMessage.visibility = View.GONE
            blocReceivedImageMessage.visibility = View.VISIBLE
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDettach() {
    }
}