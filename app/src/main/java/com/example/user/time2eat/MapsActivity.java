package com.example.user.time2eat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, FoursquareLoader.FetchItemsCallback {

    private static final String CLIENT_ID = "2RSD0BHKRAOU043MHKNMALCCN4PTJP42GWRCW1PKMXOUKOK2";
    private static final String CLIENT_SECRET = "IWJY41WDLP3H43HE5AW0CTFLRFNPRUZEVF3C5HQYQMIMJSNA";
    private static final Double DEF_LATITUDE = 50.4463956;
    private static final Double DEF_LONGITUDE = 30.552992;
    private static final String TAG = "MapsActivity";
    public final int REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 15;
    public static final String LAST_KNOWN_LOCATION = "LAST_KNOWN_LOCATION";
    public static final String RADIUS = "radius";
    public static final String VENUE_DETAILS = "venue_details";

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(DEF_LATITUDE, DEF_LONGITUDE);
    private boolean isSearchTabVisible = false;
    private CardView mCardView;
    private Animation animAppear;
    private Animation animDisappear;
    private FoursquareLoader foursquareLoader;
    private float[] distanceArray = new float[3];
    private float prevZoomLevel;
    private List<FoursquareItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationPermissionGranted = !getLocationPermission();
        if(mLocationPermissionGranted){
            initMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocationUI();
        getDeviceLocation();
    }

    private void initMap(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mCardView = findViewById(R.id.cardViewSearch);
        animAppear = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.appear);
        animDisappear = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.disappear);

        initAutocompleteFragment();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mCurrentLocation = latLngtoLocation(mMap.getCameraPosition().target);
        updateLocationUI();
        getDeviceLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mCardView != null && mCardView.getVisibility() == View.VISIBLE) {
                    mCardView.setVisibility(View.GONE);
                    mCardView.setAnimation(animDisappear);
                    mCardView.getAnimation().start();
                }
            }
        });
    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            loadMarkers();
        }
    });
    mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
        @Override
        public void onCameraMoveStarted(int i) {
           prevZoomLevel = mMap.getCameraPosition().zoom;
        }
    });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
        { @Override
            public void onCameraIdle() {
                if (mMap.getCameraPosition().target != null) {
                    mCurrentLocation = latLngtoLocation(mMap.getCameraPosition().target);
                    if (mLastKnownLocation != null) {
                        try {
                            Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                                    , mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), distanceArray);
                            if (distanceArray[0] > 700) {
                                mMap.clear();
                                loadMarkers();
                                mLastKnownLocation = mCurrentLocation;
                            }
                        } catch (IllegalArgumentException iae){
                            loadMarkers();
                        }
                    }
                }
                if (prevZoomLevel != mMap.getCameraPosition().zoom){
                    mMap.clear();
                    loadMarkers();
                }
            }
        });


}

    private Location latLngtoLocation(LatLng latLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }


    private void loadMarkers() {
        if (mMap.getCameraPosition().target != null) {
            Log.i(TAG, mMap.getCameraPosition().zoom + "");
            foursquareLoader = new FoursquareLoader(mMap.getCameraPosition().target, zoomToRadius(mMap.getCameraPosition().zoom), this);
            foursquareLoader.registerCallback(this);
            foursquareLoader.fetchData();
        }
    }

    private boolean getLocationPermission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
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
                if(mLocationPermissionGranted) {
                    startvenuesListActivity();
                } else {
                    showPermissionMessage();
                }
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
        if (mLastKnownLocation != null) {
            intent.putExtra(MapsActivity.LAST_KNOWN_LOCATION, new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        } else {
            intent.putExtra(MapsActivity.LAST_KNOWN_LOCATION, new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        }
        intent.putExtra(MapsActivity.RADIUS, zoomToRadius(mMap.getCameraPosition().zoom));
            startActivity(intent);

    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void initAutocompleteFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if(place != null) {
                    if (mMap != null) {
                        if (place.getViewport() != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 10));
                        } else
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
                    }
                }
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


    @Override
    public void itemsFetchedCallBack() {
        mItems = foursquareLoader.getVenues();
        showMarkers(mItems);

        }

    private void showMarkers(List<FoursquareItem> items) {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isBest()) {
                    Marker m = mMap.addMarker(new MarkerOptions().position
                            (new LatLng(items.get(i).getLatitude(), items.get(i).getLongitude()))
                            .title(items.get(i).getName())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    m.setTag(items.get(i));
                } else {
                    Marker m = mMap.addMarker(new MarkerOptions().position
                            (new LatLng(items.get(i).getLatitude(), items.get(i).getLongitude())).title(items.get(i).getName()));
                    m.setTag(items.get(i));
                }
            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    FoursquareItem item = (FoursquareItem) marker.getTag();
                    Intent intent = new Intent(MapsActivity.this, VenueDetailsActivity.class);
                    intent.putExtra(MapsActivity.VENUE_DETAILS, item);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }

    private int zoomToRadius(float zoom){
        Log.i(TAG, "zoomToRadius: " + 500 * (int) Math.pow(2, 15-zoom));
        return 500 * (int) Math.pow(2, 15-zoom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE){

            }
        }
    }
}

