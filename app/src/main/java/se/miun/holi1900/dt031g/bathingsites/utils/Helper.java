package se.miun.holi1900.dt031g.bathingsites.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * This class holds constants and static methods that are used by multiple classes
 */
public class Helper {
    public static final String BATHING_SITE_TABLE = "bathing_sites_table";
    public static final String BATHING_SITES_FILE = "bathingSitesFile.csv";
    public static final CharSequence DOWNLOAD_PROGRESS_DIALOG_MESSAGE = "Downloading bathing sites";

    /**
     * gets the value of a sharedpreference
     * @param key preference key
     * @param defaultValue default value of the preference
     * @param context context
     * @return value of the sharedPreference or the default value
     */
    public static String getPreferenceSummary(String key, String defaultValue,Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }
}
