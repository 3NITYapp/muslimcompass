package qiblacompass.qibladirection.finddirection.helper

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout

object GeneralUtils {

    var optionsDrawerFlag = -1
    var drawer : DrawerLayout? = null

    @JvmStatic
    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    fun showSettingsAlert(context: Context) {
        val alertDialog = AlertDialog.Builder(context)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing the Settings button.
        alertDialog.setPositiveButton(
            "Settings"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }

        // On pressing the cancel button
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            dialog.cancel()
            (context as Activity).finish()
        }

        // Showing Alert Message
        alertDialog.show()
    }

}