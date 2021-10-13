package com.example.telegram2.ui.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.example.telegram2.R
import com.example.telegram2.database.*

import com.example.telegram2.databinding.FragmentSettingsBinding
import com.example.telegram2.utillits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class Settings : BaseFragment(R.layout.fragment_settings) {
    lateinit var bindingClass: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentSettingsBinding.inflate(inflater)
        return bindingClass.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = Settings()
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        bindingClass.settingsBio.text= USER.bio
        bindingClass.settingsFullName.text= USER.fullname
        bindingClass.settingsPhoneNumber.text= USER.phone
        bindingClass.settingsStatus.text= USER.state
        bindingClass.settingsUsername.text= USER.username
        bindingClass.settingsBtnChangeUsername.setOnClickListener {
            replaceFragment(ChangeUsernameFragment())
        }
        bindingClass.settingsBtnChangeBio.setOnClickListener {
            replaceFragment(ChangeBioFragment())
    }
        bindingClass.settingsChangePhoto.setOnClickListener {
            changePhotoUser()
        }
     bindingClass.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(250,250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //создание выпадающего меню
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //слушатель выбора пунктов выпадающего меню
        when(item.itemId){
            R.id.settings_menu_exit -> {
                AppStates.updateStates(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.settings_menu_change_name ->{
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode== RESULT_OK && data !=null){
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

            putFileToStorage(uri, path){
                getUrlFromStorage(path){
                    putUrlToDataBase(it){
                        bindingClass.settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl=it
                        APP_ACTIVITY.myAppDrawer.updateHeader()

                    }
                }
            }
        }
    }


}