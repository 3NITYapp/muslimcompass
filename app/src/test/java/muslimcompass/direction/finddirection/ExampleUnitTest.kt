package muslimcompass.direction.finddirection

import android.content.Context
import android.text.format.DateFormat
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getCalendarFromPrayerTime(
        context: Context,
        prayerTime: String
    ): Calendar {

        var cal = Calendar.getInstance(TimeZone.getDefault())
        cal.timeInMillis = System.currentTimeMillis()

        var strTime = prayerTime
        if (!DateFormat.is24HourFormat(context)) {
            val display = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parse = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = parse.parse(strTime)
            if (date != null) strTime = display.format(date)
        }
        val time = strTime.split(":").toTypedArray()
        cal[Calendar.HOUR_OF_DAY] = Integer.valueOf(time[0])
        cal[Calendar.MINUTE] = Integer.valueOf(time[1])
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal
    }
}