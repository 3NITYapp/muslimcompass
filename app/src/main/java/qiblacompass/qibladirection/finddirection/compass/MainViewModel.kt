package qiblacompass.qibladirection.finddirection.compass

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel(val context: Context) : ViewModel() {

    private var SensorSendTime: Long = 0

    fun lowPass(input: Double, lastOutput: Double, dt: Double): Double {
        var elapsedTime: Double = dt - SensorSendTime
        Log.d("TIMESEND", elapsedTime.toString() + "")
        SensorSendTime = dt.toLong()
        elapsedTime = elapsedTime / 1000
        val lagConstant = 1.0
        val alpha = elapsedTime / (lagConstant + elapsedTime)
        return alpha * input + (1 - alpha) * lastOutput
    }

    fun lowPassPointerLevel(input: Double, lastOutput: Double, dt: Double): Double {
        val lagConstant = 0.25
        val alpha = dt / (lagConstant + dt)
        return alpha * input + (1 - alpha) * lastOutput
    }

}