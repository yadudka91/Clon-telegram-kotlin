package com.example.telegram2.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram2.R
import com.example.telegram2.database.CURRENT_UID
import com.example.telegram2.database.getFileFromStorage
import com.example.telegram2.ui.message_recycler_view.views.MessageView
import com.example.telegram2.utillits.WRITE_FILES
import com.example.telegram2.utillits.asTime
import com.example.telegram2.utillits.checkPermission
import com.example.telegram2.utillits.showToast
import java.io.File
import java.lang.Exception

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {


    private val blocReceivedFileMessage =
        view.findViewById<ConstraintLayout>(R.id.block_received_file_message)
    private val chatReceivedFileMessageTime =
        view.findViewById<TextView>(R.id.chat_received_file_message_time)

    private val blocUserFileMessage =
        view.findViewById<ConstraintLayout>(R.id.block_user_file_message)
    private val chatUserFileMessageTime =
        view.findViewById<TextView>(R.id.chat_user_file_message_time)

    private val chatUserFilename = view.findViewById<TextView>(R.id.chat_user_filename)
    private val chatUserBtnDownload = view.findViewById<ImageView>(R.id.chat_user_btn_download)
    private val chatUserProgressBar = view.findViewById<ProgressBar>(R.id.chat_user_progress_bar)

    private val chatReceivedFilename = view.findViewById<TextView>(R.id.chat_received_filename)
    private val chatReceivedBtnDownload = view.findViewById<ImageView>(R.id.chat_received_btn_download)
    private val chatReceivedProgressBar = view.findViewById<ProgressBar>(R.id.chat_received_progress_bar)


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocReceivedFileMessage.visibility = View.GONE
            blocUserFileMessage.visibility = View.VISIBLE
            chatUserFileMessageTime.text = view.timeStamp.asTime()
            chatUserFilename.text=view.text
        } else {
            blocUserFileMessage.visibility = View.GONE
            blocReceivedFileMessage.visibility = View.VISIBLE
            chatReceivedFileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFilename.text=view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) chatUserBtnDownload.setOnClickListener { clicktoBtnFile(view) }
        else chatReceivedBtnDownload.setOnClickListener { clicktoBtnFile(view) }


    }

    private fun clicktoBtnFile(view: MessageView) {
        if (view.from== CURRENT_UID){
            chatUserBtnDownload.visibility=View.INVISIBLE
            chatUserProgressBar.visibility=View.VISIBLE
        } else{
            chatReceivedBtnDownload.visibility=View.INVISIBLE
            chatReceivedProgressBar.visibility=View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text)
        try {
            if (checkPermission(WRITE_FILES)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if (view.from== CURRENT_UID){
                        chatUserBtnDownload.visibility=View.VISIBLE
                        chatUserProgressBar.visibility=View.INVISIBLE
                    } else{
                        chatReceivedBtnDownload.visibility=View.VISIBLE
                        chatReceivedProgressBar.visibility=View.INVISIBLE
                    }
                    }

                }
            } catch (e:Exception){
                showToast(e.message.toString())
        }
    }

    override fun onDettach() {
        chatUserBtnDownload.setOnClickListener (null)
        chatReceivedBtnDownload.setOnClickListener (null)

    }
}