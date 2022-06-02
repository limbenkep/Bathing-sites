package se.miun.holi1900.dt031g.bathingsites;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import se.miun.holi1900.dt031g.bathingsites.utils.Helper;

public class DownloadActivity extends AppCompatActivity {
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //Runtime External storage permission for saving download files
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }



        WebView webView = findViewById(R.id.bathing_sites_webview);
        webView.loadUrl(Helper.BATHING_SITES_DOWNLOAD_LINK);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        CustomProgressDialogView progressDialog = new CustomProgressDialogView("Loading...");

        progressDialog.show(getSupportFragmentManager(), "CustomProgressDialogView");
        // Our WebView should handle URL loading instead of the default handler of URL's
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
        WebView wv = (WebView)findViewById(R.id.bathing_sites_webview);
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

    @Override
    protected void onResume() {
        super.onResume();
        destroyReceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        destroyReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyReceiver();
    }



    /*private class ReadBathingSitesFromFileAsyncTask extends AsyncTask<Void, Void, ArrayList<BathingSite>> {
        private ArrayList<BathingSite> bathingSites;
        ProgressDialog progressDialog;

        ReadBathingSitesFromFileAsyncTask() {
            super();
            bathingSites = new ArrayList<>();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DownloadActivity.this);
            progressDialog.setTitle("Downloading");
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        @Override
        protected ArrayList<BathingSite> doInBackground(final Void... voids) {
            BufferedReader buffer;
            try {
                String line = "";
                File file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Helper.BATHING_SITES_FILE);
                if (file.exists()) {
                    buffer = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file.getAbsoluteFile()),
                                    "ISO-8859-1"));

                    while ((line = buffer.readLine()) != null) {
                        String splits[] = line.split(",");

                        double longitude =
                                Double.parseDouble(splits[0].replaceAll("\"", "").trim());
                        double latitude = Double.parseDouble(splits[1].replaceAll("\"", "").trim());
                        String name = splits[2].replaceAll("\"", "").trim();
                        String address = null;
                        if (splits.length == 4) {
                            address = splits[3].replaceAll("\"", "").trim();
                        }
                        BathingSite bathingSite = new BathingSite();
                        bathingSite.siteName = name;
                        bathingSite.longitude= longitude;
                        bathingSite.latitude = latitude;
                        bathingSite.address = address;
                        bathingSites.add(bathingSite);

                        if (isCancelled()) {
                            deleteFile();
                            break;
                        }
                    }
                    if (deleteFile()) {
                        Toast.makeText(DownloadActivity.this, file.getName() + " Deleted", Toast.LENGTH_SHORT);
                    }
                    buffer.close();
                } else {
                    Log.i("Here ==>>>>", "No file");
                }

            } catch (Exception e) {
                Log.d(getClass().getSimpleName().toUpperCase(), e.getMessage());
            }
            return bathingSites;
        }

        @Override
        protected void onPostExecute(final ArrayList<BathingSite> bathingSites) {
            super.onPostExecute(bathingSites);
            String msg =
                    "File reading completed with [" + bathingSites.size() + "] bathing " + "sites";

            Toast.makeText(getApplicationContext(), "Saving files to database...",
                    Toast.LENGTH_LONG).show();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        public boolean deleteFile() {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString();
            File fileDir = new File(path);
            Boolean fileDeleted = false;
            if (fileDir.isDirectory()) {
                File[] filesList = fileDir.listFiles();
                for (File file : filesList) {
                    if (file.getName().startsWith("downloadedSite")) {
                        fileDeleted = file.delete();
                    }
                }
            }
            return fileDeleted;
        }
    }

    private void processFile() {
        ReadBathingSitesFromFileAsyncTask readFile = new ReadBathingSitesFromFileAsyncTask();
        Toast.makeText(DownloadActivity.this, "Reading file contents...", Toast.LENGTH_LONG);
        readFile.execute();

        try {
            ArrayList<BathingSite> bathingSites = readFile.get();
            if (!bathingSites.isEmpty()) {

                BathingSitesRepository repository =
                        new BathingSitesRepository(getApplicationContext());

                for (BathingSite bathingSite : bathingSites) {
                    repository.insertNewBathingSite(bathingSite);
                }
                /*Globals.addToPreference(Globals.BATHING_SITES,
                        Integer.toString(repository.numberOfBathingSites()), getApplicationContext());

                Globals.toastLong(getApplicationContext(), "Saving of bathing sites completed");*/
           /* }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}