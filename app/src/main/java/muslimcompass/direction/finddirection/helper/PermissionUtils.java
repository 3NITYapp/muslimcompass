package muslimcompass.direction.finddirection.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import muslimcompass.direction.finddirection.compass.MainActivity;
import muslimcompass.direction.finddirection.permission.PermissionDetailActivity;

public class PermissionUtils {

    public static void checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionDetailActivity.startMe(context);
            }else{
                GpsTracker gpsService = new GpsTracker(context);
                Intent intent = new Intent(context, gpsService.getClass());
                if (!GeneralUtils.isMyServiceRunning(context, gpsService.getClass())) {
                    context.startService(intent);
                }
                MainActivity.startMe(context);
            }
        }else {
            MainActivity.startMe(context);
        }
    }

}