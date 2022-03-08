package qiblacompass.qibladirection.finddirection.prayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import qiblacompass.qibladirection.finddirection.R
import qiblacompass.qibladirection.finddirection.base.BaseActivity
import qiblacompass.qibladirection.finddirection.databinding.ActivityPrayerBinding
import qiblacompass.qibladirection.finddirection.helper.Constants
import qiblacompass.qibladirection.finddirection.helper.Constants.ALARM_FOR
import qiblacompass.qibladirection.finddirection.helper.Constants.KEYS
import qiblacompass.qibladirection.finddirection.helper.DateUtils
import qiblacompass.qibladirection.finddirection.helper.DialogUtils
import qiblacompass.qibladirection.finddirection.helper.PrefsUtils
import java.util.*

class PrayerActivity : BaseActivity(), DialogUtils.OnSettingsSavedListener {

    lateinit var currentlocale: Locale
    var prayers: PrayTime = PrayTime()
    var timezone: Double = 0.0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var utils: PrefsUtils
    private lateinit var binding: ActivityPrayerBinding
    private lateinit var dialog: DialogUtils
    lateinit var viewModel: PrayerViewModel

    override fun observeViewModel() {
        // nothing
    }

    override fun initViewBinding() {
        binding = ActivityPrayerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val view = binding.root
        binding.viewModel = viewModel
        setContentView(view)
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PrayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentlocale = resources.configuration.locale
        utils = PrefsUtils(this)
        dialog = DialogUtils(this, layoutInflater, this)

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(currentlocale) == ViewCompat.LAYOUT_DIRECTION_LTR
        if(isLeftToRight) {
            binding.ivCardTop.scaleX = -1f
        } else {
            binding.ivCardTop.scaleX = 1f
        }

        if (!utils.getFromPrefsWithDefault(Constants.IS_INIT, false)) {
            for (key in KEYS) {
                utils.saveToPrefs(ALARM_FOR + key, 0)
            }
            utils.saveToPrefs(Constants.IS_INIT, true)
        }

        // get prayer time
        latitude = utils.getFromPrefsWithDefault(Constants.LATITUDE, 0.0)
        longitude = utils.getFromPrefsWithDefault(Constants.LONGITUDE, 0.0)
        val tz = TimeZone.getDefault()
        val nowt = Date()
        timezone = tz.getOffset(nowt.time) / 3600000.0

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
        prayers.timeFormat = /*utils.getFromPrefsWithDefault(PrefsUtils.timeFormat, 0)*/1
        prayers.calcMethod = utils.getFromPrefsWithDefault(PrefsUtils.calcMethod, 1)
        prayers.asrJuristic = utils.getFromPrefsWithDefault(PrefsUtils.juristic, 0)
        prayers.adjustHighLats = utils.getFromPrefsWithDefault(PrefsUtils.highLats, 0)

        Log.d("timeFormatiz", "${prayers.timeNames} and ${prayers.calcMethod} and ${prayers.asrJuristic} and ${prayers.adjustHighLats}")

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

        Log.d("checktime1", viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[6]).toString())

        if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[0]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[1])) {
            binding.cardSunrise.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "sunrise")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[1]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[2])) {
            binding.cardZuhr.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "zohr")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[2]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[3])) {
            binding.cardAsr.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "asr")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[3]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[4])) {
            binding.cardMaghrib.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "maghrib")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[4]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[5])) {
            binding.cardMaghrib.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "maghrib")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[5]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[6])) {
            binding.cardIsha.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "isha")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[6])) {
            binding.cardFajr.setCardBackgroundColor(resources.getColor(R.color.highlight_color))
            Log.d("checktime", "fajr")
        }

        binding.tvFajr.text = todayPrayerTimes[0]
        binding.tvSunrise.text = todayPrayerTimes[1]
        binding.tvDuhr.text = todayPrayerTimes[2]
        binding.tvAsar.text = todayPrayerTimes[3]
        binding.tvSunset.text = todayPrayerTimes[4]
        binding.tvMaghrib.text = todayPrayerTimes[5]
        binding.tvIsha.text = todayPrayerTimes[6]
        binding.tvGregorianDate.text = DateUtils.getGregorianDate()
        binding.tvIslamicDate.text = DateUtils.writeIslamicDate()

        viewModel.updateAlarmStatus()

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