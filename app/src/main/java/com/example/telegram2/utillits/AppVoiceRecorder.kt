package com.example.telegram2.utillits

import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

class AppVoiceRecorder {

    //companion object{ //одразу обращатися к класу

        private var mMediaRecorder = MediaRecorder()
        private lateinit var mFile:File
        private lateinit var mMessageKey:String

        // фоновий визов, щоб не підвисало
        fun startRecord(messageKey: String)  {
            try {// безпечний визов
                mMessageKey=messageKey
                createFileForRecord()
                prepareMediaRecorder() // підготувати
                mMediaRecorder.start()

            } catch (e:Exception){
                showToast(e.message.toString())
            }
        }


        private fun prepareMediaRecorder() {
            mMediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)// звідки брати звук
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)// формат
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)// кодировка
            setOutputFile(mFile.absolutePath)// де зберігати
            prepare() // підготувати
            }


        }

        private fun createFileForRecord() {
            mFile = File(APP_ACTIVITY.filesDir, mMessageKey) // обявили файл
            mFile.createNewFile() // создали файл
        }

        fun stopRecord(onSuccess:(file:File, messageKey: String)->Unit){
            try {
                mMediaRecorder.stop()
                onSuccess(mFile, mMessageKey)
            } catch (e:Exception){
                showToast(e.message.toString())
                mFile.delete()
            }

        }
        fun releaseRecord(){
            try {
                mMediaRecorder.release()//видалити із памяті
            } catch (e:Exception){
                showToast(e.message.toString())
            }

        }
    }
