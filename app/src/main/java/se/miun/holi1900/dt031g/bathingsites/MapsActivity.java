package se.miun.holi1900.dt031g.bathingsites;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import se.miun.holi1900.dt031g.bathingsites.databinding.ActivityMapsBinding;
import se.miun.holi1900.dt031g.bathingsites.db.BathingSite;
import se.miun.holi1900.dt031g.bathingsites.db.BathingSitesRepository;
import se.miun.holi1900.dt031g.bathingsites.utils.Helper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient; //dependency must be added for this to work
    private Location deviceLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final float DEFAULT_ZOOM = 15f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        Log.d(TAG, "onCreate: ending");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

       //new Handler(Looper.getMainLooper()).post(this::checkLocationPermission);*/
       // checkLocationPermission();
        getDeviceLocation();
        //markBathingSitesOnMap(getBathingSites());
    }

    /**
     * checks if location permission is granted,
     * and request for permission if the permission is not granted
     * check if an explanation should be shown or not.
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION );
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
                markBathingSitesOnMap(getBathingSites());
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

   private void getDeviceLocation() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.d(TAG, "onSuccess: ");
                    if(location != null){
                        Log.d(TAG, "onSuccess: lat " + location.getLatitude());
                        deviceLocation = new Location(location);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Current device location."));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        markBathingSitesOnMap(getBathingSites());
                    }
                }
            });
        }else{
            requestLocationPermission();
        }
    }

    private double getSearchRadius(){
        String radiusText = Helper.getPreferenceSummary(getString(R.string.radius_key),
                getString(R.string.radius_default_value), getApplicationContext());
        String[] splits = radiusText.toUpperCase().split("K");

        try {
            return Double.parseDouble(splits[0].trim());
        }catch(Exception e){
            Toast.makeText(this, "Search radius format is wrong.", Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    private boolean isBathingSiteWithinSearchRange(BathingSite site){
        double radius = getSearchRadius();
        double deviceLat = deviceLocation.getLatitude();
        double deviceLon = deviceLocation.getLongitude();
        double latitudeUpperRange = deviceLat + radius;
        double longitudeUpperRange = deviceLon + radius;
        double latitudeLowerRange = deviceLat - radius;
        double longitudeLowerRange = deviceLon - radius;
        double siteLat = site.latitude;
        double siteLon = site.longitude;
        return (siteLat >= latitudeLowerRange) && (siteLat <= latitudeUpperRange)
                && (siteLon >= longitudeLowerRange) && (siteLon <= longitudeUpperRange);
    }

    private ArrayList<BathingSite> getBathingSites(){
        ArrayList<BathingSite> selected = new ArrayList<>();
        new BathingSitesRepository(this).getAllBathingSites()
                .observe(this, new Observer<List<BathingSite>>() {
                    @Override
                    public void onChanged(List<BathingSite> sites) {
                        Log.d(TAG, "onChanged: List size = " + (sites.size()));
                        if(sites.size() != 0){
                            for(int i=0; i<sites.size(); i++){
                                BathingSite bathingSite = sites.get(i);
                                if(isBathingSiteWithinSearchRange(bathingSite)){
                                    selected.add(bathingSite);
                                }
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "No bathing sites found in the database.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return selected;
    }

    private void markBathingSitesOnMap(ArrayList<BathingSite>sites){
        if(sites != null && sites.size()>0){
            for(int i=0; i<sites.size(); i++){
                BathingSite site = sites.get(i);
                LatLng latLng = new LatLng(site.latitude, site.longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(site.siteName));
            }
        }
    }

}