package com.example.telegram2

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.example.telegram2.database.AUTH
import com.example.telegram2.database.initFirebase
import com.example.telegram2.database.initUser

import com.example.telegram2.databinding.ActivityMainBinding
import com.example.telegram2.ui.screens.MainFragment
import com.example.telegram2.ui.screens.register.EnterphonenumberFragment
import com.example.telegram2.ui.objects.AppDrawer
import com.example.telegram2.utillits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityMainBinding
      lateinit var myAppDrawer:AppDrawer
    lateinit var myToolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        APP_ACTIVITY = this
        initFirebase()
        initUser{


            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }




    private fun initFunc() {
        // функиция инициализирует функциональность приложения
        setSupportActionBar(myToolbar)
        if(AUTH.currentUser !=null){
            myAppDrawer.creat()
            replaceFragment(MainFragment(), false)
        } else{
            replaceFragment(EnterphonenumberFragment(), false)
        }
    }



    private fun initFields() {
        myToolbar= bindingClass.myToolbar
        myAppDrawer = AppDrawer()

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateStates(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateStates(AppStates.OFFLINE)
    }

    override fun sendOrderedBroadcast(
        intent: Intent,
        receiverPermission: String?,
        resultReceiver: BroadcastReceiver?,
        scheduler: Handler?,
        initialCode: Int,
        initialData: String?,
        initialExtras: Bundle?
    ) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }
}