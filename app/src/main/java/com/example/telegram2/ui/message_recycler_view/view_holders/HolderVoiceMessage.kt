package com.example.telegram2.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram2.R
import com.example.telegram2.database.CURRENT_UID
import com.example.telegram2.ui.message_recycler_view.views.MessageView
import com.example.telegram2.utillits.AppVoicePlayer
import com.example.telegram2.utillits.asTime

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val mAppVoicePlayer = AppVoicePlayer()
    private val blocReceivedVoiceMessage =
        view.findViewById<ConstraintLayout>(R.id.block_received_voice_message)
    private val chatReceivedVoiceMessageTime =
        view.findViewById<TextView>(R.id.chat_received_voice_message_time)

    private val blocUserVoiceMessage =
        view.findViewById<ConstraintLayout>(R.id.block_user_voice_message)
    private val chatUserVoiceMessageTime =
        view.findViewById<TextView>(R.id.chat_user_voice_message_time)

    private val chatReceivedBtnPlay = view.findViewById<ImageView>(R.id.chat_received_btn_play)
    private val chatReceivedBtnStop = view.findViewById<ImageView>(R.id.chat_received_btn_stop)

    private val chatUserBtnPlay = view.findViewById<ImageView>(R.id.chat_user_btn_play)
    private val chatUserBtnStop = view.findViewById<ImageView>(R.id.chat_user_btn_stop)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocReceivedVoiceMessage.visibility = View.GONE
            blocUserVoiceMessage.visibility = View.VISIBLE
            chatUserVoiceMessageTime.text = view.timeStamp.asTime()
        } else {
            blocUserVoiceMessage.visibility = View.GONE
            blocReceivedVoiceMessage.visibility = View.VISIBLE
            chatReceivedVoiceMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.init()
        if (view.from == CURRENT_UID) {
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop {
                        chatUserBtnStop.setOnClickListener (null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE
                }
            }
        } else {
            chatReceivedBtnPlay.setOnClickListener {
                chatReceivedBtnPlay.visibility = View.GONE
                chatReceivedBtnStop.visibility = View.VISIBLE
                chatReceivedBtnStop.setOnClickListener {
                    stop {
                        chatReceivedBtnStop.setOnClickListener (null)
                        chatReceivedBtnPlay.visibility = View.VISIBLE
                        chatReceivedBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    override fun onDettach() {
        chatUserBtnPlay.setOnClickListener(null)
        chatReceivedBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }

    fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop { function() }
    }
}