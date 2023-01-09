package muslimcompass.direction.finddirection

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import android.content.res.Configuration
import muslimcompass.direction.finddirection.helper.LocaleManager

@HiltAndroidApp
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base!!))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}