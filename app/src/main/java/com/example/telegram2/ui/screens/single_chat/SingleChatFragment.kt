package com.example.telegram2.ui.screens.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegram2.R
import com.example.telegram2.database.*
import com.example.telegram2.databinding.FragmentSingleChatBinding
import com.example.telegram2.models.CommonModel
import com.example.telegram2.models.UserModel
import com.example.telegram2.ui.screens.BaseFragment
import com.example.telegram2.ui.message_recycler_view.views.AppViewFactory
import com.example.telegram2.utillits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleChatFragment(private val contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var bindingClass: FragmentSingleChatBinding
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUser:DatabaseReference
    private lateinit var mRefMessages:DatabaseReference
    private lateinit var mAdapter:SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChieldEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder// екземпляр
    private lateinit var mBottomSheetBehavior:BottomSheetBehavior<*>
    private lateinit var bsb: View



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentSingleChatBinding.inflate(inflater)
        return bindingClass.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        bsb = bindingClass.choiceUploadId.bottomSheetChoice
        mBottomSheetBehavior= BottomSheetBehavior.from(bsb)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder=AppVoiceRecorder()
        mSwipeRefreshLayout = bindingClass.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)

        bindingClass.chatInputMessage.addTextChangedListener(AppTextWatcher{
            val string = bindingClass.chatInputMessage.text.toString()
            if (string.isEmpty() || string=="Запись"){
                bindingClass.chatBtnSendMessage.visibility = View.GONE
                bindingClass.chatBtnAttach.visibility = View.VISIBLE
                bindingClass.chatBtnVoice.visibility= View.VISIBLE
            } else{
                bindingClass.chatBtnSendMessage.visibility = View.VISIBLE
                bindingClass.chatBtnAttach.visibility = View.GONE
                bindingClass.chatBtnVoice.visibility= View.GONE
            }
        })
        bindingClass.chatBtnAttach.setOnClickListener { attach() }

        //отдельный поток
        CoroutineScope(Dispatchers.IO).launch {
            bindingClass.chatBtnVoice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)){
                    if (event.action==MotionEvent.ACTION_DOWN){
                        // TODO record
                        bindingClass.chatInputMessage.setText("Запись")
                        bindingClass.chatBtnVoice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, R.color.primary))
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (event.action==MotionEvent.ACTION_UP){
                        //TODO stop record
                        bindingClass.chatInputMessage.setText("")
                        bindingClass.chatBtnVoice.colorFilter = null
                        mAppVoiceRecorder.stopRecord{file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey, contact.id, TYPE_MESSAGE_VOICE) // Uri.fromFile(file) - получить юри из файла
                            mSmoothScrollToPosition = true // опустити на низ
                        }
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // вивести
        bindingClass.choiceUploadId.btnAttachFile.setOnClickListener { attachFile() }
        bindingClass.choiceUploadId.btnAttachImage.setOnClickListener { attachImage() }

    }

    private fun attachFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)

    }


    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(250,250)
            .start(APP_ACTIVITY, this)

    }

    private fun initRecyclerView() {
    mRecyclerView=bindingClass.chatRecyclerView
        mAdapter= SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID).child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChieldEventListener{
        val message = it.getCommonModel()
            if (mSmoothScrollToPosition){
                mAdapter.addItemToBottom(AppViewFactory.getView(message)){
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            }else{
                mAdapter.addItemToTop(AppViewFactory.getView(message)){
                    mSwipeRefreshLayout.isRefreshing = false // отключить виджет який крутиться
            }
            }
        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)// ограничения количестго сообщений
        mRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)// відслідковує скрул
                    mIsScrolling= true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 10){ // координат по вертикалі
                    updateData()
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling= false
        mCountMessages +=10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

    }

    private fun initToolbar() {
        APP_ACTIVITY.bindingClass.toolbarInfo.root.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar() // обновить ТулБар
        }
        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        bindingClass.chatBtnSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = bindingClass.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendmessage(message, contact.id, TYPE_TEXT) {
                }
                bindingClass.chatInputMessage.setText(" ")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()){
            APP_ACTIVITY.bindingClass.toolbarInfo.toolbarChatFullname.text = contact.fullname
        } else APP_ACTIVITY.bindingClass.toolbarInfo.toolbarChatFullname.text = mReceivingUser.fullname
        APP_ACTIVITY.bindingClass.toolbarInfo.toolbarChatImage.downloadAndSetImage(mReceivingUser.photoUrl)
        APP_ACTIVITY.bindingClass.toolbarInfo.toolbarChatStatus.text = mReceivingUser.state
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data !=null){
            when(requestCode){
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true // опустити на низ
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFilenameFromUri (uri!!)
                    uploadFileToStorage(uri,messageKey,contact.id, TYPE_MESSAGE_FILE, filename)
                    mSmoothScrollToPosition = true // опустити на низ
                }
            }
        }
    /*if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode== Activity.RESULT_OK && data !=null){
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = getMessageKey(contact.id)
            uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
            mSmoothScrollToPosition = true // опустити на низ

        }*/
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.bindingClass.toolbarInfo.root.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAppVoiceRecorder.releaseRecord()
        mAdapter.onDestroy()
    }
}


