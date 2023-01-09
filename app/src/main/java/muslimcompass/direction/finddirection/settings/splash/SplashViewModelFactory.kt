package muslimcompass.direction.finddirection.settings.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SplashViewModelFactory(val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)){
            return SplashViewModel(context) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}