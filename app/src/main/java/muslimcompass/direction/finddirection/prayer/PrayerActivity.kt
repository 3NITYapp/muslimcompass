package muslimcompass.direction.finddirection.prayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import muslimcompass.direction.finddirection.R
import muslimcompass.direction.finddirection.base.BaseActivity
import muslimcompass.direction.finddirection.compass.MainActivity
import muslimcompass.direction.finddirection.databinding.ActivityPrayerBinding
import muslimcompass.direction.finddirection.helper.Constants
import muslimcompass.direction.finddirection.helper.Constants.ALARM_FOR
import muslimcompass.direction.finddirection.helper.Constants.KEYS
import muslimcompass.direction.finddirection.helper.DateUtils
import muslimcompass.direction.finddirection.helper.DialogUtils
import muslimcompass.direction.finddirection.helper.PrefsUtils
import java.util.*

class PrayerActivity : BaseActivity(), DialogUtils.OnSettingsSavedListener {

    var NAMAZ_NOTIFICATION_CHANNEL = 0
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

//        sendNotification(this, "a", "a")

        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/"+R.raw.azan)
        var longArray = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

        val mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(soundUri, this, mNotificationManager, true, longArray)
        }

    }

    private fun sendNotification(applicationContext: Context, key: String, time: String) {
        val utils = PrefsUtils(applicationContext)
        var position = 0
        for (i in Constants.KEYS.indices) {
            if (Constants.KEYS[i] == key)
                position = i
        }
        val title = Constants.NAME_ID[position]

        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/"+R.raw.azan)

        val id = generateRandom()

        var intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent = Intent(applicationContext, MainActivity::class.java)

        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext, id , intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "Alarm-Pray"
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.app_icon)
                .setColor(Color.TRANSPARENT)
                .setAutoCancel(true)
                .setContentTitle("Time $title")
                .setContentText("Will start at $time")
                .setSound(soundUri)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(resultPendingIntent)
        val mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.app_name), importance
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)
            if (channel.canBypassDnd()) channel.setBypassDnd(true)

//            notificationBuilder.setChannelId(channelId)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(id, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun makeNotificationChannel(
        path: Uri?,
        context: Context,
        notificationManager: NotificationManager,
        isVibrationEnabled: Boolean,
        patternVibrate: LongArray?
    ) {
        notificationManager.deleteNotificationChannel(NAMAZ_NOTIFICATION_CHANNEL.toString())
        NAMAZ_NOTIFICATION_CHANNEL++
        val channel = NotificationChannel(
            NAMAZ_NOTIFICATION_CHANNEL.toString(),
            "Namaz Adhan Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lightColor = Color.GRAY
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.vibrationPattern = patternVibrate
        channel.enableVibration(isVibrationEnabled)
        channel.description = context.resources.getString(R.string.app_name)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(path, audioAttributes)
        if (channel.canBypassDnd()) channel.setBypassDnd(true)
        notificationManager.createNotificationChannel(channel)

    }

    private fun generateRandom(): Int {
        val random = Random()
        return random.nextInt(9999 - 1000) + 1000
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
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "sunrise")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[1]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[2])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "zohr")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[2]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[3])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "asr")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[3]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[4])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "maghrib")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[4]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[5])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "maghrib")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[5]) && viewModel.checktimingsbefore(viewModel.getCurrentTime(), todayPrayerTimes[6])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
            Log.d("checktime", "isha")
        } else if(viewModel.checktimingsafter(viewModel.getCurrentTime(), todayPrayerTimes[6])) {
            binding.cardSunrise.setBackgroundResource(R.drawable.bg_button)
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