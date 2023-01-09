package muslimcompass.direction.finddirection.prayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import muslimcompass.direction.finddirection.helper.Constants.TIME_12
import muslimcompass.direction.finddirection.helper.PermissionUtils
import muslimcompass.direction.finddirection.helper.PrefsUtils
import muslimcompass.direction.finddirection.scheduler.PrayAlarmReceiver
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PrayerViewModel(val appContext: Application) : AndroidViewModel(appContext) {

    private var utils: PrefsUtils = PrefsUtils(appContext)

    fun onNextButtonClick() {
        PermissionUtils.checkPermissions(appContext)
    }

    fun checktimingsbefore(time: String, endtime: String): Boolean {
        val sdf = SimpleDateFormat(TIME_12)
        try {
            val date1 = sdf.parse(time)
            val date2 = sdf.parse(endtime)
            return date1.before(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun checktimingsafter(time: String, endtime: String): Boolean {
        val sdf = SimpleDateFormat(TIME_12)
        try {
            val date1 = sdf.parse(time)
            val date2 = sdf.parse(endtime)
            return date1.after(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun getCurrentTime() : String {
        var simpleDateFormat = SimpleDateFormat(TIME_12)
        return simpleDateFormat.format(Calendar.getInstance().time).lowercase(Locale.getDefault())
    }

    fun updateAlarmStatus() {
        val prayerAlarmReceiver = PrayAlarmReceiver()
        prayerAlarmReceiver.cancelAlarm(appContext)
        prayerAlarmReceiver.setAlarm(appContext)
    }

}