package com.cnt.police;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.adapters.EmergencyAdapter;
import com.cnt.police.models.Emergency;
import com.cnt.police.network.RetrofitClient;
import com.cnt.police.network.RetrofitInterface;
import com.cnt.police.network.requests.FcmDataPayload;
import com.cnt.police.network.requests.NotificationRequest;
import com.cnt.police.status.UserType;
import com.cnt.police.storage.MyPrefs;
import com.cnt.police.utils.MyUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PatrollingActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "PatrollingActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 3;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "isLocationUpdates";
    private GoogleMap mMap;
    private boolean permissionDenied = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private LatLng mOrigin;
    private FloatingActionButton fabgps, fabSatellite;
    private ExtendedFloatingActionButton fab_navigate, fab_backup;
    private boolean requestingLocationUpdates = false;
    private boolean isPatrolMode = false, isEmergency = false;
    private ArrayList<MarkerOptions> mMarkerOptionsArrayList;
    private Emergency mEmergency;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;
    private ArrayList<Emergency> mEmergencies;
    private RecyclerView mRecyclerEmergency;
    private EmergencyAdapter mEmergencyAdapter;
    private ListenerRegistration mListenerRegistration;
    private Map<String, LatLng> mStringLatLngMap;
    private Map<String, MarkerOptions> mStringMarkerOptionsMap;
    private MyPrefs mPrefs;
    private RetrofitInterface api;

//    private List<LatLng> mLatLngList;
//    private ArrayList<MarkerOptions> mOtherEmergencyMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrolling);
        fabgps = findViewById(R.id.fab_gps);
        fabSatellite = findViewById(R.id.fab_satellite);
        fab_navigate = findViewById(R.id.fab_navigate);
        fab_backup = findViewById(R.id.fab_backup);
        mRecyclerEmergency = findViewById(R.id.recyclerEmergency);

        mPrefs = new MyPrefs(this);
        api = RetrofitClient.getFCMInstance().create(RetrofitInterface.class);


        mMarkerOptionsArrayList = new ArrayList<>();
        mEmergencies = new ArrayList<>();
        mStringLatLngMap = new HashMap<>();
        mStringMarkerOptionsMap = new HashMap<>();

        mEmergency = new Emergency();
        mEmergency.setUserType(UserType.POLICE_USER.name());
        mEmergency.setCity(mPrefs.getCity());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getUid();

        updateValuesFromBundle(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null)
            return;
        mapFragment.getMapAsync(this);


        mEmergencyAdapter = new EmergencyAdapter(this, mEmergencies);
        mRecyclerEmergency.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerEmergency.setItemAnimator(new DefaultItemAnimator());
        mRecyclerEmergency.setAdapter(mEmergencyAdapter);

        getCurrentEmergenciesFromFireStore();

        fabgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrigin != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 20));
                }
            }
        });

        fabSatellite.setOnClickListener(v -> {
            if (mStringLatLngMap.isEmpty()) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 10));
            } else if (mStringLatLngMap.size() == 1) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 10));
            } else {
                zoomRoute(mStringLatLngMap);
            }
        });

        fab_navigate.setOnClickListener(v -> {
            if (isPatrolMode) {
                stopLocationUpdates();
                mMap.setTrafficEnabled(false);
                isPatrolMode = false;
                fab_navigate.shrink();
                mMap.clear();
                for (MarkerOptions options : mMarkerOptionsArrayList) {
                    mMap.addMarker(options);
                }
                for (MarkerOptions options : mStringMarkerOptionsMap.values()) {
                    mMap.addMarker(options);
                }
                mMap.addMarker(new MarkerOptions()
                        .position(mOrigin).title("My Location")
                );
            } else {
                checkLocationSettings();
                isPatrolMode = true;
                fab_navigate.setText("Patrolling ON");
                fab_navigate.extend();
            }

        });

        fab_backup.setOnClickListener(v -> {
            if (ToggleEmergencyView()) {
                mEmergency.setTimestamp(new Timestamp(Calendar.getInstance().getTime()));
                updateEmergencyStatusInFirestore(mEmergency);
            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mMap.setTrafficEnabled(true);
                mMap.clear();
                for (MarkerOptions options : mMarkerOptionsArrayList) {
                    mMap.addMarker(options);
                }
                for (MarkerOptions options : mStringMarkerOptionsMap.values()) {
                    mMap.addMarker(options);
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.i(TAG, "onLocationResult: " + location.getLatitude());

                    mMap.addMarker(new MarkerOptions().title("Police Car")
                            .position(mOrigin)
                            .rotation(location.getBearing())
                            .anchor(0.5f, 0.5f)
                            .icon(MyUtils.bitmapDescriptorFromVector(PatrollingActivity.this, R.drawable.ic_car_icon)));

                    MarkerOptions options = new MarkerOptions().title("New Location")
                            .position(mOrigin)
                            .icon(MyUtils.bitmapDescriptorFromVector(PatrollingActivity.this, R.drawable.ic_dot));

                    mMarkerOptionsArrayList.add(options);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 15));
                    if (isEmergency) {
                        mEmergency.setEmergencyLocation(new GeoPoint(mOrigin.latitude, mOrigin.longitude));
                        mEmergency.setTimestamp(new Timestamp(Calendar.getInstance().getTime()));
                        updateEmergencyLocationInFireStore(mEmergency);
                    }
                }
            }
        };
        createLocationRequest();
        checkLocationSettings();

    }

    private void updateEmergencyLocationInFireStore(Emergency emergency) {
        db.collection("Emergency")
                .document(uid)
                .set(emergency)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Updated Location");
                        }
                    }
                });
    }

    private void getCurrentEmergenciesFromFireStore() {
        mListenerRegistration = db.collection("Emergency")
                .whereEqualTo("city", mEmergency.getCity())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "onEvent: " + error.getMessage());
                            return;
                        }
                        if (value == null)
                            return;
                        if (value.getDocuments().size() == 1 && value.getDocuments().get(0).getId().equals(uid)) {
                            //Listener called on Updating My Own Location so Return
                            return;
                        }
                        if (!mEmergencies.isEmpty()) {
                            mEmergencies.clear();
                            mEmergencyAdapter.notifyDataSetChanged();
                        }
                        if (!mStringLatLngMap.isEmpty()) {
                            mStringLatLngMap.clear();
                        }
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            boolean isEm = snapshot.getBoolean("isEmergency");
                            if (snapshot.getId().equals(uid)) {
                                isEmergency = isEm;
                                updateMyEmergencyView();
                                continue;
                            }
                            if (isEm) {
                                Emergency emergency = snapshot.toObject(Emergency.class);
                                if (emergency == null)
                                    continue;
                                mEmergencies.add(emergency);
                                addOtherEmergenciesMarkersOnMap(emergency, snapshot.getId());
                            } else {
                                mStringLatLngMap.remove(snapshot.getId());
                                mStringMarkerOptionsMap.remove(snapshot.getId());
                                updateEmergencyMarkers();
                            }
                        }
                        if (mOrigin != null) {
                            mStringLatLngMap.put("OriginLoc", mOrigin);
                        }
                        zoomRoute(mStringLatLngMap);
                        mEmergencyAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateEmergencyMarkers() {
        mMap.clear();
        for (MarkerOptions options : mStringMarkerOptionsMap.values()) {
            mMap.addMarker(options);
        }
        if (mOrigin != null) {
            mMap.addMarker(new MarkerOptions().position(mOrigin));
        }
    }

    private void addOtherEmergenciesMarkersOnMap(Emergency emergency, String id) {
        LatLng latLng = new LatLng(emergency.getEmergencyLocation().getLatitude(), emergency.getEmergencyLocation().getLongitude());
        mStringLatLngMap.put(id, latLng);
        if (emergency.getUserType().equals(UserType.POLICE_USER.name())) {
            MarkerOptions options = new MarkerOptions()
                    .icon(MyUtils.bitmapDescriptorFromVector(PatrollingActivity.this, R.drawable.ic_police_backup_marker))
                    .position(latLng)
                    .title(emergency.getUserType());
            mMap.addMarker(options);
            mStringMarkerOptionsMap.put(id, options);
        } else {
            MarkerOptions options = new MarkerOptions()
                    .icon(MyUtils.bitmapDescriptorFromVector(PatrollingActivity.this, R.drawable.ic_user_help_marker))
                    .position(latLng)
                    .title(emergency.getUserType());
            mMap.addMarker(options);
            mStringMarkerOptionsMap.put(id, options);
        }
    }

    private void updateMyEmergencyView() {
        if (isEmergency) {
            fab_backup.setText("Cancel Backup");
        } else {
            fab_backup.setText("Call Backup");
        }
    }

    private boolean ToggleEmergencyView() {
        if (mOrigin == null) {
            Toast.makeText(PatrollingActivity.this, "Location Unavailable", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmergency) {
            isEmergency = false;
            fab_backup.setText("Call Backup");
            mEmergency.setEmergencyLocation(new GeoPoint(mOrigin.latitude, mOrigin.longitude));

        } else {
            mEmergency.setEmergencyLocation(new GeoPoint(mOrigin.latitude, mOrigin.longitude));
            isEmergency = true;
            fab_backup.setText("Cancel Backup");
        }
        mEmergency.isEmergency = isEmergency;
        return true;
    }

    private void updateEmergencyStatusInFirestore(Emergency mEmergency) {

        db.collection("Emergency")
                .document(uid)
                .set(mEmergency)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Updated Emergency");
                            if (mEmergency.isEmergency) {
                                sendNotificationToOthers();
                            }
                        }
                    }
                });
    }

    private void sendNotificationToOthers() {
        NotificationRequest request = new NotificationRequest();
        request.setTo("/topics/".concat(mPrefs.getCity()));
        FcmDataPayload data = new FcmDataPayload();
        data.setTitle("UNDER EMERGENCY");
        data.setBody("Someone Reported Emergency in CNT Police App!");
        data.setClick_action("Emergency");
        data.setSenderUID(mPrefs.getUID());
        request.setData(data);
        Call<ResponseBody> call = api.sendNotification(request);
        Log.i(TAG, "sendNotificationToOthers: " + call.request().url().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.i(TAG, "onResponse: " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.i(TAG, "onResponse: ERROR CODE " + response.code() + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

//        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(@NonNull LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                if (isPatrolMode) {
                    startLocationUpdates();
                } else {
                    enableMyLocation();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(PatrollingActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.

                    }
                }
            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location == null) {
                                    return;
                                }
                                mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(mOrigin).title("My Location")

                                );
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 12));
                                mStringLatLngMap.put("OriginLoc", mOrigin);
                            }
                        });
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void zoomRoute(Map<String, LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute.values())
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

//    private void getCurrentLocation() {
//        CancellationTokenSource cts =new CancellationTokenSource();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mFusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
//                    cts.getToken())
//                    .addOnCompleteListener(new OnCompleteListener<Location>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Location> task) {
//                            if(task.isSuccessful()){
//                                Location location = task.getResult();
//                                mOrigin = new LatLng(location.getLatitude(),location.getLongitude());
//                                mMap.addMarker(new MarkerOptions()
//                                        .position(mOrigin).title("My Location")
//
//                                );
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 12));
//                            }
//                        }
//                    });
//        }
//    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        requestingLocationUpdates = true;
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                permissionDenied = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                if (isPatrolMode) {
                    startLocationUpdates();
                } else {
                    enableMyLocation();
                }

            } else if (resultCode == RESULT_CANCELED) {
                if (isPatrolMode) {
                    startLocationUpdates();
                } else {
                    enableMyLocation();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
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

    private void showMissingPermissionError() {
//            PermissionUtils.PermissionDeniedDialog
//                    .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
//        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
        }
    }
}