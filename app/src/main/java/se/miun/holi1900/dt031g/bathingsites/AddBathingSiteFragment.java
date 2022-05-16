package se.miun.holi1900.dt031g.bathingsites;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class AddBathingSiteFragment extends Fragment {
    final String TAG = "AddBathingSiteFragment";
    public  static  final String READ_FILE_ERROR = "Error reading Weather";
    EditText name;
    EditText description;
    EditText address;
    EditText latitude;
    EditText longitude;
    RatingBar grade;
    EditText waterTemp;
    EditText tempDate;
    Drawable error_icon;
    final String WEATHER_BASE_URL = "https://dt031g.programvaruteknik.nu/bathingsites/weather.php?";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bathing_site, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_options, menu);
        inflater.inflate(R.menu.settings_menu, menu);
        inflater.inflate(R.menu.menu_show_weather, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.save_form_option){
            return saveEnteredInformation();
        }
        if(item.getItemId()==R.id.delete_form_option){
            clearEntryFields();
            return true;
        }
        if(item.getItemId()==R.id.show_weather){
            boolean downloaded = showWeather();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get references to the editTexts in the fragment
        name = view.findViewById(R.id.nameEntry);
        description  = view.findViewById(R.id.descriptionEntry);
        address = view.findViewById(R.id.addressEntry);
        latitude = view.findViewById(R.id.latitudeEntry);
        longitude = view.findViewById(R.id.longitudeEntry);
        grade = view.findViewById(R.id.gradeEntry);
        waterTemp = view.findViewById(R.id.tempEntry);
        tempDate = view.findViewById(R.id.dateWaterEntry);

        error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        if(error_icon !=null){
            error_icon.setBounds(0, 0, error_icon.getIntrinsicWidth(), error_icon.getIntrinsicHeight());
        }
    }

    /**
     * validate if all mandatory fields are filled. Name must be fields and
     * address and/or longitude must be filled.
     * @return true if mandatory fields are filled else false
     */
    public boolean validateMandatoryField(){
        String nameText = name.getText().toString();
        String addressText = address.getText().toString();
        String longitudeText  = longitude.getText().toString();
        String latitudeText = latitude.getText().toString();

        //If name and address are entered, the entries are valid
        if(!nameText.equals("") && !addressText.equals("")){
            return true;
        }
        //If name latitude and longitude are entered, the entries are valid
        if(!nameText.equals("") && !latitudeText.equals("") && !longitudeText.equals("")){
            return true;
        }

        //Drawable error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        //if the name field is empty, display error icon and message
        if(nameText.equals("")){
            name.setError(getString(R.string.name), error_icon);
        }
        //if the address field is empty, display error icon and message
        if(addressText.equals("")){
            address.setError(getString(R.string.address), error_icon);
        }
        //if the latitude field is empty, display error icon and message
        if(latitudeText.equals("")){
            latitude.setError(getString(R.string.latitude), error_icon);
        }
        //if the longitude field is empty, display error icon and message
        if(longitudeText.equals("")){
            longitude.setError(getString(R.string.longitude), error_icon);
        }
        return false;
    }

    public boolean saveEnteredInformation(){
        if(validateMandatoryField()){
            //TODO: Implement
            Context context = requireContext();
            CharSequence text = "Name: " + name.getText() + "\n"
                    + "Description: " + description.getText() + "\n"
                    + "Address: " + address.getText() + "\n"
                    + "Latitude: " + latitude.getText() + "\n"
                    + "Longitude: " + longitude.getText() + "\n"
                    + "Grade: " + grade.getRating();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return true;
        }
        return false;
    }

    /**
     * clear all editText text and rating
     */
    public  void clearEntryFields(){
        name.getText().clear();
        description.getText().clear();
        address.getText().clear();
        latitude.getText().clear();
        longitude.getText().clear();
        grade.setRating(0f);
        waterTemp.getText().clear();
        tempDate.getText().clear();
    }

    /**
     * Get weather information for the location of the new bathing site.
     * if only coordinates or address is entered used the entered location to get weather
     * if both cordinates and address are etered use coordinates for better precision
     * use the location to build a URL upon the WEATHER_BASE_URL and download the weather
     * information from the URL
     * @return return true if address or coordinates are provided else false
     */
    public boolean showWeather(){
        String addressText = address.getText().toString();
        String longitudeText  = longitude.getText().toString();
        String latitudeText = latitude.getText().toString();
        Uri uri;

        if(!latitudeText.equals("") && !longitudeText.equals("")){
            uri = Uri.parse(WEATHER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("lat", latitudeText)
                    .appendQueryParameter("lon", longitudeText)
                    .build();
            DownloadWeatherAsyncTask downloadWeatherAsyncTask = new DownloadWeatherAsyncTask();
            try {
                downloadWeatherAsyncTask.execute(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                //Log.e(TAG, "showWeather: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        else if(!addressText.equals("")){
            uri = Uri.parse(WEATHER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("q", addressText)
                    .build();
            DownloadWeatherAsyncTask downloadWeatherAsyncTask = new DownloadWeatherAsyncTask();
            try {
                downloadWeatherAsyncTask.execute(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                Log.e(TAG, "showWeather: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        else {
            Toast.makeText(getContext(), "Address or coordinates are required to get weather information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    // A dialog fragment for showing an weather

    /**
     * This class is a custom dialogFragment thet will be used to display weather information to user.
     */
    public static class ShowWeatherDialogFragment extends DialogFragment {
        private final String message;

        public ShowWeatherDialogFragment(String message) {
            super();
            this.message = message;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.show_weather_dialog_title)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {})
                    .create();
        }
    }

    /**
     * This class implements the methods to execute the async task to download weather information from a URL
     * The class gets the URL as input, displays a progress dialog on screen will the weather information
     * is downloaded in the background. When the download is done, the information if displayed on dialog Fragment
     */
    private class DownloadWeatherAsyncTask extends AsyncTask<URL, Integer, String> {
        //custom progressDialog for show progress of file download
        private CustomProgressDialogView progressDialog;

        /**
         * display progress dialog
         */
        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialogView();
            progressDialog.show(getChildFragmentManager(), "CustomProgressDialogView");
        }

        /**
         * creates an HttpsURLConnection and read the data from the passed url and return the data as a string
         * @param urls url from which weather information will be read.
         * @return a String containing the weather information if reading is successful or empty string if reading fails
         */
        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[urls.length-1];
            HttpsURLConnection connection;
            try {
                // get a connection from url

                connection = (HttpsURLConnection) url.openConnection();

                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;

                //read files line for line as long as action is not cancelled and there is still lines to read.
                while (!isCancelled() && (inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
                connection.disconnect();
                return sb.toString();
            } catch (IOException e) {
                Log.e("WeatherDownloadAsyncTask", "Error reading weather file: " + e.getMessage());
                return "";
            }
        }


        /**
         * Convert the weather information string to JSON and Extracts overcast and temperature
         * and display them on a dialog fragment after dismissing the progress dialog
         * @param result String containing weather information returned from doInBackgrond method
         */
        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

            if(result.isEmpty()){
                new ShowWeatherDialogFragment(AddBathingSiteFragment.READ_FILE_ERROR);
            }
            else{
                try {
                    JSONObject jsondata = (JSONObject) new JSONTokener(result).nextValue();
                    JSONArray jsonWeather = jsondata.getJSONArray("weather");
                    JSONObject weather = (JSONObject) jsonWeather.get(0);
                    String overcast = weather.getString("main");

                    JSONObject jsonMain = jsondata.getJSONObject("main");
                    double temp = jsonMain.getDouble("temp");
                    Log.d("WeatherDownloadAsyncTask", "overcast: " + overcast + " temp: " + temp);
                    new ShowWeatherDialogFragment("Overcast " + overcast + "\n" + temp).show(getChildFragmentManager(), TAG);

                } catch (JSONException e) {
                    Log.e("WeatherDownloadAsyncTask", "Error error converting read string to json: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}