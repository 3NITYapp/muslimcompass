package qiblacompass.qibladirection.finddirection.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigPreferences {
    private static final String MAIN_CONFIG = "application_settings";
    public static final String LOCATION_INFO = "location_information",
            QUIBLA_DEGREE = "quibla_degree";

    public static int getQuibla(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        int degree = sharedPreferences.getInt(QUIBLA_DEGREE, -1);
        return degree;
    }

    public static void setQuibla(Context context, int degree) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putInt(QUIBLA_DEGREE, degree);
        editor.commit();
    }

}