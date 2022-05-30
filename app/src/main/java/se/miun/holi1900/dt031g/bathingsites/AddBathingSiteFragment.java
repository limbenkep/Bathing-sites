package se.miun.holi1900.dt031g.bathingsites;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import se.miun.holi1900.dt031g.bathingsites.db.BathingSite;
import se.miun.holi1900.dt031g.bathingsites.db.BathingSitesRepository;


public class AddBathingSiteFragment extends Fragment {
    final String TAG = "AddBathingSiteFragment";
    public static final String READ_FILE_ERROR = "Error reading Weather";
    final Calendar myCalendar= Calendar.getInstance();
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
    final String WEATHER_ICON_URL = "https://openweathermap.org/img/w/";

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
        if (item.getItemId() == R.id.save_form_option) {
            saveEnteredInformation();
            return true;
        }
        if (item.getItemId() == R.id.delete_form_option) {
            clearEntryFields();
            return true;
        }
        if (item.getItemId() == R.id.show_weather) {
            boolean downloaded = showWeather();
            return true;
        }
        if (item.getItemId() == R.id.settings_option) {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get references to the editTexts in the fragment
        name = view.findViewById(R.id.nameEntry);
        description = view.findViewById(R.id.descriptionEntry);
        address = view.findViewById(R.id.addressEntry);
        latitude = view.findViewById(R.id.latitudeEntry);
        longitude = view.findViewById(R.id.longitudeEntry);
        grade = view.findViewById(R.id.gradeEntry);
        waterTemp = view.findViewById(R.id.tempEntry);
        tempDate = view.findViewById(R.id.dateWaterEntry);
        error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        if (error_icon != null) {
            error_icon.setBounds(0, 0, error_icon.getIntrinsicWidth(), error_icon.getIntrinsicHeight());
        }

        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            setDateWaterTemp();
        };
        tempDate.setOnClickListener(view12 -> new DatePickerDialog(requireContext(),date,myCalendar.get(Calendar.YEAR),myCalendar
                .get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    /**
     * sets the value of the date water temperature field
     */

    private void setDateWaterTemp(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        tempDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    /**
     * validate if all mandatory fields are filled. Name must be fields and
     * address and/or longitude must be filled.
     *
     * @return true if mandatory fields are filled else false
     */
    public boolean validateMandatoryField() {
        String nameText = name.getText().toString();
        String addressText = address.getText().toString();
        String longitudeText = longitude.getText().toString();
        String latitudeText = latitude.getText().toString();

        //If name and address are entered, the entries are valid
        if (!nameText.equals("") && !addressText.equals("")) {
            return true;
        }
        //If name latitude and longitude are entered, the entries are valid
        if (!nameText.equals("") && !latitudeText.equals("") && !longitudeText.equals("")) {
            return true;
        }

        //Drawable error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        //if the name field is empty, display error icon and message
        if (nameText.equals("")) {
            name.setError(getString(R.string.name), error_icon);
        }
        //if the address field is empty, display error icon and message
        if (addressText.equals("")) {
            address.setError(getString(R.string.address), error_icon);
        }
        //if the latitude field is empty, display error icon and message
        if (latitudeText.equals("")) {
            latitude.setError(getString(R.string.latitude), error_icon);
        }
        //if the longitude field is empty, display error icon and message
        if (longitudeText.equals("")) {
            longitude.setError(getString(R.string.longitude), error_icon);
        }
        return false;
    }

    /**
     * saves new bathing site to Room database and return to MainActivity.
     * If any of the obligatory fields are not provided, the bathing sites is not saved
     * If bathing site with the same latitude and longitude as the new bathing site already
     * exist in the database, the bathing site is not saved and a message is displayed.
     */
    public void saveEnteredInformation() {
        if (validateMandatoryField()) {
            BathingSite bathingSite = new BathingSite();
            bathingSite.siteName = name.getText().toString();
            bathingSite.description = description.getText().toString();
            bathingSite.address = address.getText().toString();
            String lat = latitude.getText().toString();
            if(!lat.isEmpty()){
                bathingSite.latitude = Integer.parseInt(lat);
            }
            String lon = longitude.getText().toString();
            if(!lon.isEmpty()){
                bathingSite.longitude = Integer.parseInt(lon);
            }
            String tmp = waterTemp.getText().toString();
            if(!tmp.isEmpty()){
                bathingSite.waterTemp = Double.parseDouble(tmp);
            }

            bathingSite.date = tempDate.getText().toString();
            bathingSite.grade = grade.getRating();
            BathingSitesRepository repo = new BathingSitesRepository(getContext());
            if(repo.BathingSiteFound(bathingSite)){

                CharSequence text = "A bathing site with latitude " + bathingSite.latitude
                        + " and longitude " + bathingSite.longitude + "already exist.";
                Toast toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                repo.insertNewBathingSite(bathingSite);
                clearEntryFields();
                requireActivity().finish();
            }
        }
    }

    /**
     * clear all editText text and rating
     */
    public void clearEntryFields() {
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
     * if both coordinates and address are entered use coordinates for better precision
     * use the location to build a URL upon the WEATHER_BASE_URL and download the weather
     * information from the URL
     *
     * @return return true if address or coordinates are provided else false
     */
    public boolean showWeather() {
        String addressText = address.getText().toString();
        String longitudeText = longitude.getText().toString();
        String latitudeText = latitude.getText().toString();
        Uri uri;
        if (!latitudeText.equals("") && !longitudeText.equals("")) {
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
        } else if (!addressText.equals("")) {
            try {
                uri = Uri.parse(WEATHER_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter("q", URLEncoder.encode(addressText, "UTF-8"))
                        .build();
                DownloadWeatherAsyncTask downloadWeatherAsyncTask = new DownloadWeatherAsyncTask();
                downloadWeatherAsyncTask.execute(new URL(uri.toString()));
            } catch (UnsupportedEncodingException|MalformedURLException e) {
                Log.e(TAG, "showWeather: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else {
            Toast.makeText(getContext(), "Address or coordinates are required to get weather information", Toast.LENGTH_SHORT).show();
            return false;
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
         *
         * @param urls url from which weather information will be read.
         * @return a String containing the weather information if reading is successful or empty string if reading fails
         */
        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[urls.length - 1];
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
         * Convert the weather information string to JSON and Extracts overcast, temperature, and icon code
         * The icon code is used to build the url get the icon weather icon of the given location
         * the icon image is downloaded asynchronously using the class DownloadImageFromUrlAsyncTask.
         * When all downloads are completed, the progress dialog is dismissed
         * The weather icon, overcast and temperature are displayed in a dialog fragment.
         *
         * @param result String containing weather information returned from doInBackground method
         */
        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

            if (result.isEmpty()) {
                Toast.makeText(getContext(), READ_FILE_ERROR, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonData = (JSONObject) new JSONTokener(result).nextValue();
                    JSONArray jsonWeather = jsonData.getJSONArray("weather");
                    JSONObject weather = (JSONObject) jsonWeather.get(0);
                    String overcast = weather.getString("main");
                    String icon = weather.getString("icon");
                    JSONObject jsonMain = jsonData.getJSONObject("main");
                    int temp =(int) jsonMain.getDouble("temp");
                    URL iconUrl = new URL(WEATHER_ICON_URL + icon + ".png");
                    DownloadImageFromUrlAsyncTask drawableFromUrlAsyncTask = new DownloadImageFromUrlAsyncTask();
                    // Get icon image from url using an Asynchronously. The .get() method get the
                    // results synchronously byt stopping this thread until until the image is gotten.
                    // This ensures that the drawable is obtained before the ShowWeatherDialog is displayed
                    final Bitmap bitmap= drawableFromUrlAsyncTask.execute(iconUrl).get();
                    Drawable weatherIcon = new BitmapDrawable(getResources(), bitmap);

                    //Display weather in a dialog box.
                    new ShowWeatherDialogFragment("Overcast " + overcast + "\n" + temp
                            + "°", weatherIcon).show(getChildFragmentManager(), TAG);
                    //Log.d("WeatherDownloadAsyncTask", "overcast: " + overcast + " temp: " + temp);
                } catch (JSONException e) {
                    //Log.e("WeatherDownloadAsyncTask", "Error error converting read string to json: " + e.getMessage());
                    Toast.makeText(getContext(), "No weather information was found for the entered location.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (MalformedURLException | ExecutionException | InterruptedException e) {
                    //Log.d("WeatherDownloadAsyncTask", "Malformed url");
                    Toast.makeText(getContext(), READ_FILE_ERROR, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This class downloads an image from a provided URL
     */
    private static class DownloadImageFromUrlAsyncTask extends AsyncTask<URL, Integer, Bitmap> {
        private static final String TAG = "DrawableFromUrlAsyncTas";

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[urls.length - 1];
            Log.d(TAG, "in on doInBackground " + url.toString());
            HttpsURLConnection connection;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
}