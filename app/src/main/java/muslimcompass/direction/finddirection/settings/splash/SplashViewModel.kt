package muslimcompass.direction.finddirection.settings.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import muslimcompass.direction.finddirection.helper.PermissionUtils

class SplashViewModel(val appContext: Context) : ViewModel() {

    fun onNextButtonClick() {
        PermissionUtils.checkPermissions(appContext)
    }

}