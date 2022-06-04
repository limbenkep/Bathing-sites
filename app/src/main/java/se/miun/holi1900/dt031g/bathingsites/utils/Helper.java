package se.miun.holi1900.dt031g.bathingsites.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import se.miun.holi1900.dt031g.bathingsites.SettingsFragment;

public class Helper {
    public static final String NEW_BATHING_SITE_PREFERENCE_KEY = "new_bathing_site";
    public static final String SHOW_WEATHER_PREFERENCE_KEY = "Fetch_weather";
    public static final String BATHING_SITE_TABLE = "bathing_sites_table";
    //public static final String BATHING_SITES_DOWNLOAD_LINK = ;
    public static final String BATHING_SITES_FILE = "bathingSitesFile.csv";
    public static final CharSequence DOWNLOAD_PROGRESS_DIALOG_MESSAGE = "Downloading bathing sites";

    public static String getPreferenceSummary(String key, String defaultValue, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }
}
