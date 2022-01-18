package qiblacompass.qibladirection.finddirection.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import qiblacompass.qibladirection.finddirection.BuildConfig

object DrawerHelper {

    fun moreApps(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Quick+Apps"))
        context.startActivity(intent)
    }

    fun rateApp(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val appLink = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(appLink)
    }

    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Accurate Qibla Direction")
        var shareMessage = "\nAccurate Qibla Direction\n\n"
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    }

}