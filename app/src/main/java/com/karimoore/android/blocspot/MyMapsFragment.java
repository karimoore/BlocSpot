package com.karimoore.android.blocspot;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyMapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MyMapsFragment";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String mCurrentAddress;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;
    private AddressResultReceiver mResultReceiver;


    protected List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 21000; // 1 mile, 1.6 km

    public void update(List<Point> points) {
        // This is when we need to change the list and map to the new list
        mapPoints.clear();
        mapPoints.addAll(points);
        mMap.clear();
        addMarkers(points);
    }


    public class AddressResultReceiver extends ResultReceiver {
        private static final String TAG = "AddressResultReceiver";
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mCurrentAddress = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d(TAG, "Address returned is: " + mCurrentAddress);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }

        }
    }

        protected void startIntentService() {
            mResultReceiver = new AddressResultReceiver(new Handler());
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            getActivity().startService(intent);//?
    }

    public static List<Point> mapPoints = new ArrayList<>();
    public static List<Category> mCategories = new ArrayList<>();
    public static final MyMapsFragment newInstance(List<Point> points, List<Category> categories)
    {
        mCategories.addAll(categories);
        mapPoints.addAll(points);
        MyMapsFragment f = new MyMapsFragment();
        Bundle localBundle = new Bundle(2);
        localBundle.putSerializable("MARKERS", (Serializable) mapPoints);
        localBundle.putSerializable("CATEGORY", (Serializable) mCategories);
        f.setArguments(localBundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        populateGeofenceList();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        // and add the markers
        addMarkers(mapPoints);

    }


    void addMarkers(List<Point> points){
        List<Category> categories = new ArrayList<Category>();
        categories.addAll(mCategories);
        for (int i = 0; i < points.size(); i++) {
            LatLng pointOfInterest = new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude());
            mMap.addMarker(
                    new MarkerOptions()
                            .position(pointOfInterest)
                            .title(points.get(i).getName() + String.valueOf(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(categories.get((int) points.get(i).getCatId() - 1).getMarkerColor())));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pointOfInterest));

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "TODO:Could ask user for permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String latitude = (String.valueOf(mLastLocation.getLatitude()));
            String longitude = (String.valueOf(mLastLocation.getLongitude()));
                // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Log.d(TAG,  "No geocode available");
                    return;
            }

            if (true){//mAddressRequested) {  TO DO
                startIntentService();
            }
        }


        createLocationRequest();  //interval checks on change in location
        mRequestingLocationUpdates = true;  // TO DO
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }




        //------
        // Geofencing
        //--------
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),  // may not want initial trigger??
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // Successfully registered
                        /*if(mCallback != null){
                            mCallback.onGeofencesRegisteredSuccessful();
                        }*/

                    } else if (status.hasResolution()) {
                        // Google provides a way to fix the issue
                    /*
                    status.startResolutionForResult(
                            mContext,     // your current activity used to receive the result
                            RESULT_CODE); // the result code you'll look for in your
                    // onActivityResult method to retry registering
                    */
                    } else {
                        // No recovery. Weep softly or inform the user.
                        Log.e(TAG, "Registering failed: " + status.getStatusMessage());
                    }
                }
            }); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.d(TAG, String.valueOf(securityException)+ ": App is not using right permissions, ACCESS_FINE_LOCATION");
        }




   }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {

        float distance = 0.0f;
        // Has it really changed?
        if (BlocSpotApplication.getSharedInstance().getCurrentLocation() != null){
            // make sure we have changed locations
            distance = location.distanceTo(BlocSpotApplication.getSharedInstance().getCurrentLocation());
        } else {


            BlocSpotApplication.getSharedInstance().setCurrentLocation(location);
            // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //updateUI();
            //mMap.clear();

            // Map current address
            MarkerOptions mp = new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("My position")
                    .snippet(mCurrentAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.push_pin_home));
            // ;

            mMap.addMarker(mp);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 5));


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

        //shouldn't I change boolean requestingupdates now to false

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }


    /**
     * This populates geofence data.
     */
    public void populateGeofenceList() {

        for (int i = 0; i < mapPoints.size(); i++) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(mapPoints.get(i).getName())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            mapPoints.get(i).getLatitude(),
                            mapPoints.get(i).getLongitude(),
                            GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                            // Create the geofence.
                    .build());
        }
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Context c = getActivity().getBaseContext();
        Intent intent = new Intent(getActivity().getBaseContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity().getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
