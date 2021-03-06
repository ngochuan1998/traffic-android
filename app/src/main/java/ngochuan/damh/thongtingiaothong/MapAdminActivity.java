package ngochuan.damh.thongtingiaothong;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ngochuan.damh.thongtingiaothong.PermissionUtils;
import ngochuan.damh.thongtingiaothong.R;
import ngochuan.damh.thongtingiaothong.Retrofit.IMyService;
import ngochuan.damh.thongtingiaothong.Retrofit.RetrofitClient;
import ngochuan.damh.thongtingiaothong.model.LoginResponse;
import ngochuan.damh.thongtingiaothong.model.User;
import ngochuan.damh.thongtingiaothong.model.UserLocation;
import ngochuan.damh.thongtingiaothong.model.UsersLocationResponse;
import retrofit2.Retrofit;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapAdminActivity extends AppCompatActivity
        implements
//        SharedPreferences.OnSharedPreferenceChangeListener,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;
    SearchView searchView;

    private User user;
    private static final int REQUEST_CALL = 1;

    IMyService iMyService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public Context applicationContext;

    LatLng searchResultLatLng = null;

    //    BackgroundService mService=null;
    boolean mBound = false;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @SerializedName("id")

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.user = new User(extras.getString("id"));
        }

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        // use this to start and trigger a service
//        applicationContext = this.getApplicationContext();
//        Intent i = new Intent(applicationContext, BackgroundService.class);
//        applicationContext.startService(i);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_admin);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.adMap);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        searchView = findViewById(R.id.searchLoc);
        if (searchView != null) {
            Log.e("TEST", "Search view is not null");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
                    List<Address> addressList = null;
                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(MapAdminActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address address = addressList.get(0);
                                Log.e("debug", "here");
                                if (address != null) {
                                    Log.e("debug", "here 3");
                                    searchResultLatLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        map.clear();
//                        map.addMarker(new MarkerOptions()
//                                .position(latLng)
//                                .title(latLng.toString()));
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResultLatLng, 16));
//                                    new Timer().scheduleAtFixedRate(new TimerTask(){
//                                        @Override
//                                        public void run(){
//                                            getUsersLocation();
//
//                                        }
//                                    },0,10000);
                                    Log.e("debug", "here 2");
                                }
                            }
                        } catch (IOException e) {
                            Log.e("MapAdminActivity", "[ERROR] getFromLocationName " + e.getMessage());
                        }
                    }
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            getUsersLocation();
                        }
                    }, 0, 10000);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        } else {
            Log.e("TEST", "Search view is null");
        }


    }

//    @Override
//    protected void onStart() {
//        this.getUsersLocation();
//        super.onStart();
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                makePhoneCall(marker.getTitle());
                return false;
            }
        });
        enableMyLocation();
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                getUsersLocation();
//            }
//        }, 0, 10000);
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initialize Location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        // Initialize geoCoder
                        Geocoder geocoder = new Geocoder(MapAdminActivity.this, Locale.getDefault());
                        // Init address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(),1
                        );
                        LatLng myLocation = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
                    } catch (IOException e) {
                        System.out.println("[ERROR]" + e.getMessage());
//                        e.printStackTrace();
                    }
                }
            }
        });
        this.getUsersLocation();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }

        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    private void getUsersLocation() {
        compositeDisposable.add(iMyService.getUsersLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Toast.makeText(this, "Get users location Failed!", Toast.LENGTH_SHORT).show();
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String respone) {
                        UsersLocationResponse usersLocationResponse = new UsersLocationResponse();
                        try {
                            Gson g = new Gson();
                            usersLocationResponse = g.fromJson(respone, UsersLocationResponse.class);
                        } catch (Exception e) {
                            System.out.println("[ERROR] Get users location" + e.getMessage());
                            return;
                        }
                        Toast.makeText(MapAdminActivity.this, "Get users location", Toast.LENGTH_SHORT).show();
                        Log.e("Log", usersLocationResponse.toString());
                                // Delete markers
                        map.clear();

                        try {
                            if (map != null) {
                                if (searchResultLatLng != null) {
                                    map.addMarker(new MarkerOptions()
                                            .position(searchResultLatLng)
                                            .title(searchResultLatLng.toString()));
                                }
                                // Add markers
                                for (UserLocation userLocation : usersLocationResponse.userLocations) {
                                    LatLng latLng = new LatLng(userLocation.latitude, userLocation.longitude);
                                    map.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(userLocation.id)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.abc))
                                    );
                                }
                            }
                        } catch(Exception e) {
                            Log.e("Debug", "[ERROR] getUsersLocation" + e.getMessage());
                        }
                    }
                })
        );
    }

    private void makePhoneCall(String id) {
        if (ContextCompat.checkSelfPermission(MapAdminActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapAdminActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + id;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
        Toast.makeText(MapAdminActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
    }
}