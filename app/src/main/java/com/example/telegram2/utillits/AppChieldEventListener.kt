package com.example.telegram2.utillits

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AppChieldEventListener (val onSucces:(DataSnapshot) -> Unit):ChildEventListener{
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        onSucces(snapshot)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
       // TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
       // TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
       // TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
      //  TODO("Not yet implemented")
    }


}