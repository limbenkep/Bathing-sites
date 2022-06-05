package se.miun.holi1900.dt031g.bathingsites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.net.URL;
import java.util.Objects;

/**
 * This class manages the settings of this application. It manages three settings;
 * A setting preference that holds the url from which BathingSite will be downloaded and
 * allows user to enter new url
 * A setting preference that holds the url from which weather information will be downloaded and
 *  allows user to enter new url
 *  A setting preference that holds the radius from current device location within which BathingSite
 *  should be displayed on the map allows user to change the radius
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";
    private String weatherKey;
    private String bathingSiteKey;
    private String mapRadiusKey;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        weatherKey = getString(R.string.fetch_weather_key);
        bathingSiteKey = getString(R.string.bathing_site_key);
        mapRadiusKey = getString(R.string.radius_key);

        //Get SharedPreferences
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        //Get the references to the three bathing sites
        EditTextPreference weatherUrl = findPreference(weatherKey);
        EditTextPreference bathingSiteUrl = findPreference(bathingSiteKey);
        EditTextPreference mapRadius = findPreference(mapRadiusKey);

        //For all  three preferences, if they are not null,
        // set preference inputType, set preferenceSummary with value from share preference and
        // setOnPreferenceListener to check and eliminate some invalid inputs before they can be
        // stored in the sharedPreference
        if(weatherUrl != null && sharedPreferences != null){
            weatherUrl.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI));
            weatherUrl.setSummary(sharedPreferences.getString(weatherKey, getDefaultValue(weatherKey)));
            weatherUrl.setOnPreferenceChangeListener((preference, newValue) -> {
                if(newValue == null || newValue.toString().isEmpty() || !isValidUrl(newValue.toString())){
                    Toast.makeText(requireContext(), "Invalid url entry.Please enter a url",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            });
        }
        if(bathingSiteUrl != null && sharedPreferences != null){
            bathingSiteUrl.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI));
            //load preference summary
            bathingSiteUrl.setSummary(sharedPreferences.getString(bathingSiteKey, getDefaultValue(bathingSiteKey)));
            bathingSiteUrl.setOnPreferenceChangeListener((preference, newValue) -> {
                if(newValue == null || newValue.toString().isEmpty() || !isValidUrl(newValue.toString())){
                    Toast.makeText(requireContext(), "Invalid url entry.Please enter a url",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            });
        }

        if(mapRadius != null && sharedPreferences != null){
            //This preference should take only numbers and decimals for input
            mapRadius.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL));
            //load preference summary
            mapRadius.setSummary(sharedPreferences.getString(mapRadiusKey, getDefaultValue(mapRadiusKey)));
            mapRadius.setOnPreferenceChangeListener((preference, newValue) -> {
                if(newValue == null || newValue.toString().isEmpty()){
                    Toast.makeText(requireContext(), "Invalid url entry. Enter a number.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Gets the default values for the different preferences
     * @param key preference key
     * @return default value
     */
    private String getDefaultValue(String key){
        if(Objects.equals(key, weatherKey)){
            return getString(R.string.fetch_weather_settings_default_summary);
        }
        if(Objects.equals(key, bathingSiteKey)){
            return getString(R.string.download_bathing_site_url);
        }
        if(Objects.equals(key, mapRadiusKey)){
            return getString(R.string.radius_default_value);
        }
        return "";
    }

    /**
     * checks is url is valid or malformed
     * @param url url to be checked
     * @return true if valid else false
     */
    private boolean isValidUrl(String url){
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates preference summary when preference is changed.
     * @param sharedPreferences sharedPreference containing stored value
     * @param key key to the preference that has changed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Log.d(TAG, "onSharedPreferenceChanged: updated value "+ sharedPreferences.getString(key, ""));

        Preference preference = findPreference(key);
        String defaultValue = getDefaultValue(key);
        if(preference != null){
            preference.setSummary(sharedPreferences.getString(key, defaultValue));
        }
    }
}