package com.example.telegram2.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram2.R
import com.example.telegram2.database.*
import com.example.telegram2.databinding.FragmentContactsBinding
import com.example.telegram2.models.CommonModel
import com.example.telegram2.ui.screens.single_chat.SingleChatFragment
import com.example.telegram2.utillits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView


class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {


    private lateinit var bindingClass:FragmentContactsBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers:DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentContactsBinding.inflate(inflater)
        return bindingClass.root
    }


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title= "Контакты"
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = bindingClass.contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(CURRENT_UID)

        //настройка адаптера, где указываем какие данные и откуда получать
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java).build()

            // адаптер принимает данные и отображает в холдере
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                //запускается тогда, когда адаптер получает доступ к вю груп
            val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
            return ContactsHolder(view)
            }

            // заполняем холдер
            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()){
                        holder.name.text= model.fullname
                    } else

                    holder.name.text = contact.fullname
                    holder.status.text=contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {
                        replaceFragment(SingleChatFragment(model))
                    }

                }
                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers]=mRefUsersListener

            }
        }
        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()


    }

// холдер для захвата ViewGroup
    class ContactsHolder(view: View): RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo:CircleImageView = view.findViewById(R.id.contact_photo)


    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
    }
}


