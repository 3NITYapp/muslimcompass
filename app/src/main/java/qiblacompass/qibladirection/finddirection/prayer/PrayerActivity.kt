package qiblacompass.qibladirection.finddirection.prayer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import qiblacompass.qibladirection.finddirection.databinding.ActivityPrayerBinding
import qiblacompass.qibladirection.finddirection.helper.Constants
import qiblacompass.qibladirection.finddirection.helper.DateUtils
import qiblacompass.qibladirection.finddirection.helper.DialogUtils
import qiblacompass.qibladirection.finddirection.helper.PrefsUtils
import java.util.*

class PrayerActivity : AppCompatActivity(), DialogUtils.OnSettingsSavedListener {

    lateinit var currentlocale: Locale
    lateinit var prayers: PrayTime
    var timezone: Double = 0.0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var utils: PrefsUtils
    private lateinit var binding: ActivityPrayerBinding
    private lateinit var dialog: DialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentlocale = resources.configuration.locale
        utils = PrefsUtils(this)
        dialog = DialogUtils(this, layoutInflater, this)

        if(currentlocale.language.equals("en")) {
            binding.ivCardTop.scaleX = -1f
        } else {
            binding.ivCardTop.scaleX = 1f
        }

        // get prayer time
        latitude = utils.getFromPrefsWithDefault(Constants.LATITUDE, 0.0)
        longitude = utils.getFromPrefsWithDefault(Constants.LONGITUDE, 0.0)
        val tz = TimeZone.getDefault()
        val nowt = Date()
        timezone = tz.getOffset(nowt.time) / 3600000.0
        prayers = PrayTime()

        /*prayers.timeFormat = PrayTime.TIME_12
        prayers.calcMethod = PrayTime.KARACHI
        prayers.asrJuristic = PrayTime.SHAFII
        prayers.adjustHighLats = PrayTime.ANGLE_BASED*/

        getPrayerTimes(prayers, latitude, longitude, timezone)

        binding.ivSettings.setOnClickListener {
            dialog.showSettingDialog()
        }

    }

    private fun getPrayerTimes(
        prayers: PrayTime,
        latitude: Double,
        longitude: Double,
        timezone: Double
    ) {
        prayers.timeFormat = utils.getFromPrefsWithDefault(PrefsUtils.timeFormat, 0)
        prayers.calcMethod = utils.getFromPrefsWithDefault(PrefsUtils.calcMethod, 1)
        prayers.asrJuristic = utils.getFromPrefsWithDefault(PrefsUtils.juristic, 0)
        prayers.adjustHighLats = utils.getFromPrefsWithDefault(PrefsUtils.highLats, 0)

        Log.d("timeFormatiz", "${prayers.timeFormat} and ${prayers.calcMethod} and ${prayers.asrJuristic} and ${prayers.adjustHighLats}")

        prayers.lat = latitude
        prayers.lng = longitude

        val offsets =
            intArrayOf(0, 0, 0, 0, 0, 0, 0) // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}

        prayers.tune(offsets)

        // Today's date
        val calToday = Calendar.getInstance()
        calToday.time = Date()

        val todayPrayerTimes = prayers.getPrayerTimes(
            calToday,
            latitude, longitude, timezone
        )

        binding.tvFajr.text = todayPrayerTimes[0]
        binding.tvSunrise.text = todayPrayerTimes[1]
        binding.tvDuhr.text = todayPrayerTimes[2]
        binding.tvAsar.text = todayPrayerTimes[3]
        binding.tvSunset.text = todayPrayerTimes[4]
        binding.tvMaghrib.text = todayPrayerTimes[5]
        binding.tvIsha.text = todayPrayerTimes[6]
        binding.tvGregorianDate.text = DateUtils.getGregorianDate()
        binding.tvIslamicDate.text = DateUtils.writeIslamicDate()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSettingsSaved(isSaved: Boolean) {
        getPrayerTimes(prayers, latitude, longitude, timezone)
    }

    companion object {
        @JvmStatic
        fun startMe(context: Context) {
            val intent = Intent(context, PrayerActivity::class.java)
            context.startActivity(intent)
        }
    }
}