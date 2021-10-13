package com.example.telegram2.ui.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.telegram2.R
import com.example.telegram2.ui.screens.ContactsFragment
import com.example.telegram2.ui.screens.Settings
import com.example.telegram2.utillits.APP_ACTIVITY
import com.example.telegram2.database.USER
import com.example.telegram2.utillits.downloadAndSetImage
import com.example.telegram2.utillits.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

// Обект реализующий боковое меню Navigation Drawer
class AppDrawer() {
    lateinit var myDrawer: Drawer
    lateinit var myHeader: AccountHeader
    lateinit var myDrawerLayout:DrawerLayout
    lateinit var myCurrentProfile:ProfileDrawerItem


    fun creat (){
        //Создание бокового меню
        initLoader()
        createHeader()
        creatDrawer()
        myDrawerLayout=myDrawer.drawerLayout
    }

    fun disableDrawer(){
        // Отключение выдвигающего меню
        myDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.myToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    fun enableDrawer(){
        //Включение выдвигающего меню
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        myDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.myToolbar.setNavigationOnClickListener {
            myDrawer.openDrawer()
        }

    }

    private fun creatDrawer() {
        // Создание драйвера
        myDrawer = DrawerBuilder().withActivity(APP_ACTIVITY).withToolbar(APP_ACTIVITY.myToolbar).withActionBarDrawerToggle(true).withSelectedItem(-1)
            .withAccountHeader(myHeader)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100).withIconTintingEnabled(true).withName("Создать групу" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_create_groups), PrimaryDrawerItem().withIdentifier(101).withIconTintingEnabled(true).withName("Создать секретный чат" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_secret_chat), PrimaryDrawerItem().withIdentifier(102).withIconTintingEnabled(true).withName("Создать канал" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_create_channel), PrimaryDrawerItem().withIdentifier(103).withIconTintingEnabled(true).withName("Контакты" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_contacts), PrimaryDrawerItem().withIdentifier(104).withIconTintingEnabled(true).withName("Звонки" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_phone), PrimaryDrawerItem().withIdentifier(105).withIconTintingEnabled(true).withName("Избранное" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_favorites), PrimaryDrawerItem().withIdentifier(106).withIconTintingEnabled(true).withName("Настройки")
                .withSelectable(false).withIcon(R.drawable.ic_menu_settings), DividerDrawerItem(), PrimaryDrawerItem().withIdentifier(107).withIconTintingEnabled(true).withName("Пригласить друзей" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_invate), PrimaryDrawerItem().withIdentifier(108).withIconTintingEnabled(true).withName("Вопросы о Telegram" )
                .withSelectable(false).withIcon(R.drawable.ic_menu_help)

            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener{
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)

                    return false
                }
            }).build()
    }

    private fun clickToItem(position:Int){
        when (position) {
            7 -> replaceFragment(Settings())
            4 -> replaceFragment(ContactsFragment())
        }

    }

    private fun createHeader() {
        // Создание хедера
        myCurrentProfile = ProfileDrawerItem()
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)

        myHeader= AccountHeaderBuilder().withActivity(APP_ACTIVITY).withHeaderBackground(R.drawable.header)
            .addProfiles(myCurrentProfile).build()
    }
     fun updateHeader(){
         // Обновление хедера
        myCurrentProfile
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        myHeader.updateProfile(myCurrentProfile)
    }
    private fun initLoader( ){
        // Инициализация лоадера для загрузки картинки в хедер
        DrawerImageLoader.init(object : AbstractDrawerImageLoader(){
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }
        })
    }
}