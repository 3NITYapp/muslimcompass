package qiblacompass.qibladirection.finddirection.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import qiblacompass.qibladirection.finddirection.helper.PermissionUtils

class SplashViewModel(val appContext: Context) : ViewModel() {

    fun onNextButtonClick() {
        PermissionUtils.checkPermissions(appContext)
    }

}