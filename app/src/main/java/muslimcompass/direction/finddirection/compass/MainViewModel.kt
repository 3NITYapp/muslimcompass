package muslimcompass.direction.finddirection.compass

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import muslimcompass.direction.finddirection.helper.DialogUtils
import muslimcompass.direction.finddirection.prayer.PrayerActivity

class MainViewModel(val context: Context) : ViewModel() {

    private var SensorSendTime: Long = 0

    fun lowPass(input: Double, lastOutput: Double, dt: Double): Double {
        var elapsedTime: Double = dt - SensorSendTime
        Log.d("TIMESEND", elapsedTime.toString() + "")
        SensorSendTime = dt.toLong()
        elapsedTime /= 1000
        val lagConstant = 1.0
        val alpha = elapsedTime / (lagConstant + elapsedTime)
        return alpha * input + (1 - alpha) * lastOutput
    }

    fun lowPassPointerLevel(input: Double, lastOutput: Double, dt: Double): Double {
        val lagConstant = 0.25
        val alpha = dt / (lagConstant + dt)
        return alpha * input + (1 - alpha) * lastOutput
    }

    fun onPrayerTimeButtonClicked() {
        PrayerActivity.startMe(context)
    }

    fun onLanguageClicked() {
        val dialogUtils = DialogUtils(context, (context as Activity).layoutInflater)
        dialogUtils.showLanguagesDialog()
    }

}