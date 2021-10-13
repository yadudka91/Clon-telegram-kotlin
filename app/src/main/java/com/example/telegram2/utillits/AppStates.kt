package com.example.telegram2.utillits

import com.example.telegram2.database.*


enum class AppStates(val states:String) {
    ONLINE ("в сети"),
    OFFLINE ("был недавно"),
    TYPING ("печатает");

    companion object{
        fun updateStates (appStates:AppStates){
            // Функция принимает состояние и записывает в базу даных
            if (AUTH.currentUser !=null){
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATE)
                    .setValue(appStates.states)
                    .addOnSuccessListener { USER.state = appStates.states }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }

        }
    }

}