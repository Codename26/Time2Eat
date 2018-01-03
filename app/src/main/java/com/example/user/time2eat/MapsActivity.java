package com.example.user.time2eat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String CLIENT_ID = "2RSD0BHKRAOU043MHKNMALCCN4PTJP42GWRCW1PKMXOUKOK2";
    private static final String CLIENT_SECRET = "IWJY41WDLP3H43HE5AW0CTFLRFNPRUZEVF3C5HQYQMIMJSNA";
    private static final Double DEF_LATITUDE = 50.4463956;
    private static final Double DEF_LONGITUDE = 30.552992;
    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 15;
    public static final String LAST_KNOWN_LOCATION = "LAST_KNOWN_LOCATION";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private String provider;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng (DEF_LATITUDE,DEF_LONGITUDE);
    private boolean isSearchTabVisible = false;
    private CardView mCardView;
    private Animation animAppear;
    private Animation animDisappear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationPermissionGranted = !getLocationPermission();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        Criteria criteria = new Criteria();
        provider = mLocationManager.getBestProvider(criteria, false);
        if(mLocationPermissionGranted) {
            @SuppressLint("MissingPermission")
            Location location = mLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.d("Location", location.toString());
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mCardView = findViewById(R.id.cardViewSearch);
        animAppear = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.appear);
        animDisappear = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.disappear);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                    if (mCardView != null && mCardView.getVisibility() == View.VISIBLE){
                        mCardView.setVisibility(View.GONE);
                        mCardView.setAnimation(animDisappear);
                        mCardView.getAnimation().start();
                }
            }
        });
        updateLocationUI();
        getDeviceLocation();
    }

    private boolean getLocationPermission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest
                    .permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            } else {
                showPermissionMessage();
            }
        }
    }

    private void showPermissionMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle(R.string.attention)
                .setMessage(R.string.permission_request)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocationPermission();
                    }
                })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.venues_list:
                startvenuesListActivity();
                return true;
            case R.id.search:
                if (mCardView != null && mCardView.getVisibility() == View.VISIBLE){
                    mCardView.setVisibility(View.GONE);
                    mCardView.clearAnimation();
                    mCardView.setAnimation(animDisappear);
                    mCardView.getAnimation().start();
                    return true;
                }

                mCardView.setVisibility(View.VISIBLE);
                mCardView.setAnimation(animAppear);
                mCardView.getAnimation().start();
                isSearchTabVisible = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startvenuesListActivity() {
        Intent intent = new Intent(MapsActivity.this, VenuesListActivity.class);
        intent.putExtra(MapsActivity.LAST_KNOWN_LOCATION, mLastKnownLocation);
        startActivity(intent);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
