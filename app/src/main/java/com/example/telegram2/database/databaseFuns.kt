package com.example.telegram2.database

import android.net.Uri
import com.example.telegram2.R
import com.example.telegram2.models.CommonModel
import com.example.telegram2.models.UserModel
import com.example.telegram2.utillits.APP_ACTIVITY
import com.example.telegram2.utillits.AppValueEventListener
import com.example.telegram2.utillits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.ArrayList

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()

}

inline fun putUrlToDataBase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(
        CHILD_PHOTO_URL
    ).setValue(url)
        .addOnSuccessListener{ function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url:String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri).addOnSuccessListener{ function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    //Функция добовляет номер телефона с ИД в базу данных
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }

                        REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(CHILD_FULLNAME)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

// Функция преобразовувает полученые данные из Firebase в модель CommonModel
fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java)?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java)?: UserModel()

fun sendmessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID" // current - поточний
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID" // receiving - отримання
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUsername(newUserName:String) {
    //обновление username в базе даных у текущего пользователя
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful){
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

fun deleteOldUsername(newUserName:String) {// перевірка кода
    //удаление старого username из базы даных
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
        //.addOnCompleteListener {
          //  if (it.isSuccessful){
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                APP_ACTIVITY.supportFragmentManager.popBackStack()// назад
                USER.username= newUserName
            }.addOnFailureListener { showToast(it.message.toString()) } // якщо помилка то вивести тост
        }

fun setBioToDatabase(newBio: String) {// перевірка правильності кода
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
        //.addOnCompleteListener {
          //  if (it.isSuccessful){
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                USER.bio = newBio
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }.addOnFailureListener { showToast(it.message.toString()) }
        }

fun setNameToDatabase(fullname: String) { //код виконаний правильно
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                USER.fullname=fullname
                APP_ACTIVITY.myAppDrawer.updateHeader()
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(receivingUserID: String, fileUrl: String, messageKey: String, typeMessage: String, filename: String) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID" // current - поточний
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID" // receiving - отримання


    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    .child(id).push().key.toString()

fun uploadFileToStorage(uri: Uri, messageKey: String, receivedID:String, typeMessage:String, filename: String = "") {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(receivedID, it, messageKey, typeMessage, filename)
        }
    }
}
fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path= REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)// путь
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast( it.message.toString()) }
}