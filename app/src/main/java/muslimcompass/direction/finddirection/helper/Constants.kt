package muslimcompass.direction.finddirection.helper

import muslimcompass.direction.finddirection.R

object Constants {

    val EXTRA_PRAYER_NAME = "prayer_name"
    val EXTRA_PRAYER_TIME = "prayer_time"
    val ONE_MINUTE = 60000
    val FIVE_MINUTES = ONE_MINUTE * 5

    val FAJR = "Fajr"
    val SUNRISE = "Sunrise"
    val DHUHR = "Dhuhr"
    val ASR = "Asr"
    val SUNSET = "Sunset"
    val MAGHRIB = "Maghrib"
    val ISHA = "Isha"

    val ALARM_FOR = "alarm_for_"
    val ALARM_ID = 1010
    val LOCATION_ID = 1011
    val IS_INIT = "app_init"

    var KEYS = arrayOf(
        FAJR,
        SUNRISE,
        DHUHR,
        ASR,
        SUNSET,
        MAGHRIB,
        ISHA
    )

    var NAME_ID = arrayOf(
        FAJR,
        SUNRISE,
        DHUHR,
        ASR,
        SUNSET,
        MAGHRIB,
        ISHA
    )

    val TIME_12 by lazy {"hh:mm a"}

    val colorsArray = intArrayOf(R.color.first, R.color.second, R.color.third, R.color.fourth, R.color.fifth)
    val compassFaces = intArrayOf(R.drawable.compass_one, R.drawable.compass_two, R.drawable.compass_three, R.drawable.compass_four, R.drawable.compass_five)
    val translateIcon = intArrayOf(R.drawable.ic_translate_first, R.drawable.ic_translate_second, R.drawable.ic_translate_third, R.drawable.ic_translate_forth, R.drawable.ic_translate_fifth)
    val languages = intArrayOf(R.string.russian, R.string.english, R.string.arabic, R.string.spanish, R.string.french, R.string.turkish, )

    const val QUIBLA_DEGREE = "quibla_degree"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"

}