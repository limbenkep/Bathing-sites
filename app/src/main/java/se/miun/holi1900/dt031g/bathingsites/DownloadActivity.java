package se.miun.holi1900.dt031g.bathingsites;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import se.miun.holi1900.dt031g.bathingsites.db.BathingSite;
import se.miun.holi1900.dt031g.bathingsites.db.BathingSitesRepository;
import se.miun.holi1900.dt031g.bathingsites.utils.Helper;

/**
 * This class uses a webview to load a website from which csv files containing bathing sites can be
 * downloaded. The Url for the website is gotten from Settings preferences.
 * This class is responsible for loading the website, downloads the file when clicked and
 * read the bathing sites from the downloaded file and sove them in the database.
 */
public class DownloadActivity extends AppCompatActivity {
    private BroadcastReceiver receiver;
    private WebView webView;

    /**
     * Sets layout, sets webview to handle URL loading instead of the default handler of URL's
     * gets url to website from which bathing sites will be downloaded
     * sets a download listener to the webView
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        // Requesting Permission to access External Storage
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 33);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
        }
        webView = findViewById(R.id.bathing_sites_webview);
        //get link to download bathing sites from settings stored in sharedpreference
        String downloadLink = Helper.getPreferenceSummary(getString(R.string.bathing_site_key), getString(R.string.download_bathing_site_url), getApplicationContext());
        webView.loadUrl(downloadLink);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        CustomProgressDialogView progressDialog = new CustomProgressDialogView("Loading...");
        progressDialog.show(getSupportFragmentManager(), "CustomProgressDialogView");
        //WebView should handle URL loading instead of the default handler of URL's
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }
        });
        downloadBathingSitesFile();
    }

    /**
     * Sets a download listener to the webView
     * Downloads file asynchronously, name file and save in download folder.
     * Registers a BroadCastReceiver  to send notification when download is completed.
     * Displays a progress dialog during download
     */
    private void downloadBathingSitesFile(){
        //handle downloading
        webView.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                CustomProgressDialogView dialog = new CustomProgressDialogView(Helper.DOWNLOAD_PROGRESS_DIALOG_MESSAGE);

                dialog.show(getSupportFragmentManager(), "CustomProgressDialogView");

                new  Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        String state = Environment.getExternalStorageState();
                        boolean spaceFound =  Environment.MEDIA_MOUNTED.equals(state);
                        if(spaceFound){
                            DownloadManager.Request request = new DownloadManager.Request(
                                    Uri.parse(url));
                            request.setMimeType("text/comma-separated-values");
                            String cookies = CookieManager.getInstance().getCookie(url);
                            request.addRequestHeader("cookie", cookies);
                            request.addRequestHeader("User-Agent", userAgent);
                            request.setDescription("Downloading File...");
                            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS, Helper.BATHING_SITES_FILE);
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                        }
                        else{

                            Toast.makeText(getApplicationContext(), "There is not enough space available to download file.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        //register receiver to notify when download is completed
                        receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show();
                                    saveDownloadedBathingSitesToDatabase();
                                }
                            }
                        };
                        registerReceiver(receiver,
                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
        WebView wv = findViewById(R.id.bathing_sites_webview);
        if(wv.canGoBack()){
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * unregister receiver
     */
    private void destroyReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * overrides onResume method and add code to unregister BroadCastReceiver
     */
    @Override
    protected void onResume() {
        super.onResume();
        destroyReceiver();
    }

    /**
     * overrides onRestart method and add code to unregister BroadCastReceiver
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        destroyReceiver();
    }

    /**
     * overrides onDestroy method and add code to unregister BroadCastReceiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyReceiver();
    }

    /**
     * Async task to read bathing sites from file.
     * Reads bathing sites from downloaded file,
     * and return a list of bathing sites
     */
    private class ReadBathingSitesFromFileAsyncTask extends AsyncTask<Void, Void, ArrayList<BathingSite>> {
        private static final String TAG = "ReadBathingSitesFromFil";
        private ArrayList<BathingSite> bathingSites;
        CustomProgressDialogView progressDialog;

        ReadBathingSitesFromFileAsyncTask() {
            super();
            bathingSites = new ArrayList<>();
        }


        /**
         * starts progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialogView("Reading bathing sites from file.");
            progressDialog.show(getSupportFragmentManager(), "CustomProgressDialogView");
        }

        /**
         * Reads file, parse data and create BathingSite objects
         * @param voids no parameter
         * @return list os BathingSite
         */
        @Override
        protected ArrayList<BathingSite> doInBackground(final Void... voids) {
            BufferedReader buffer;
            try {
                String inputLine;
                File file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Helper.BATHING_SITES_FILE);
                if(file.exists()){
                    buffer = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), StandardCharsets.UTF_8));
                    while (!isCancelled() && (inputLine = buffer.readLine()) != null) {
                        String[] splits = inputLine.replaceAll("\\p{C}", "").split(",");
                        String longStr= splits[0].replaceAll("\"", "").trim();
                        String latStr= splits[1].replaceAll("\"", "").trim();
                        double longitude =-1;
                        double latitude = -1;
                        try {
                            longitude =Double.parseDouble(longStr.replaceAll("\"", "").trim());
                            latitude = Double.parseDouble(latStr.replaceAll("\"", "").trim());
                        }catch (Exception e){
                            Log.e(TAG, "doInBackground: " ,e);
                        }
                        String name = splits[2].replaceAll("\"", "").trim();
                        String address = "";
                        int splitsLength = splits.length;
                        if(splitsLength ==4){
                            address = splits[3].replaceAll("\"", "").trim();
                        }
                        if ( splitsLength > 4) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(splits[3].replaceAll("\"", "").trim());
                            for(int i= 4; i<splitsLength; i++){
                                sb.append(", ");
                                sb.append(splits[i].replaceAll("\"", "").trim());
                            }
                            address = sb.toString();
                        }
                        if(latitude != -1 && longitude != -1){
                            BathingSite bathingSite = new BathingSite(name, address, latitude, longitude);
                            bathingSite.address = address;
                            bathingSites.add(bathingSite);
                        }
                    }
                    if (deleteFile()) {
                        new Handler(Looper.getMainLooper())
                                .post(()->Toast.makeText(DownloadActivity.this,
                                        file.getName() + " Deleted", Toast.LENGTH_SHORT).show());
                    }
                    buffer.close();
                }else {
                    Toast.makeText(getApplicationContext(), "Downloaded bathing sites file not found.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "doInBackground: No file to read.");
                }
            } catch (Exception e) {
                Log.e(TAG, "doInBackground", e);
            }
            return bathingSites;
        }

        /**
         * Dismiss progress dialog and display message with that download is completed
         * @param bathingSites list of BathingSites
         */
        @Override
        protected void onPostExecute(final ArrayList<BathingSite> bathingSites) {
            super.onPostExecute(bathingSites);
            String text = "File reading completed. " + bathingSites.size() + " bathing " + "sites read.";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        /**
         * deletes files in download directory that starts with "downloadedSite"
         * @return true is file is deleted else false
         */
        public boolean deleteFile() {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString();
            File fileDir = new File(path);
            boolean fileDeleted = false;
            if (fileDir.isDirectory()) {
                File[] filesList = fileDir.listFiles();
                for (File file : filesList) {
                    if (file.getName().startsWith("bathingSitesFile")) {
                        fileDeleted = file.delete();
                    }
                }
            }
            return fileDeleted;
        }
    }

    /**
     * Reads bathing sites from downloaded file in Download directory and save the bathing sites to the database
     */
    private void saveDownloadedBathingSitesToDatabase() {
        ReadBathingSitesFromFileAsyncTask readFile = new ReadBathingSitesFromFileAsyncTask();
        Toast.makeText(DownloadActivity.this, "Reading file contents...", Toast.LENGTH_LONG).show();
        readFile.execute();
        try {
            ArrayList<BathingSite> bathingSites = readFile.get();
            if (!bathingSites.isEmpty()) {
                BathingSitesRepository repository =
                        new BathingSitesRepository(getApplicationContext());
                for (BathingSite bathingSite : bathingSites) {
                    repository.insertNewBathingSite(bathingSite);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}