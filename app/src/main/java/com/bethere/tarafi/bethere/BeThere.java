package com.bethere.tarafi.bethere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.location.LocationListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BeThere extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude;
    double longitude;
    double latitude2;
    double longitude2;
    LocationManager lm;
    Criteria criteria;
    private Handler mHandler = new Handler();
    String provider;
    Location mostRecentLocation;
    ParseJSON parseJSON = new ParseJSON(this);
    GPSTracker gps = new GPSTracker(BeThere.this) {
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_there);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        criteria = new Criteria();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = lm.getBestProvider(criteria, true);
        mostRecentLocation = lm.getLastKnownLocation(provider);
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().isZoomControlsEnabled();

        if (mostRecentLocation != null) {
            LatLng loc0 = new LatLng(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc0).title("your last known location"));
        }
        // Add a marker in Casablanca center and move the camera
        LatLng Casablanca = new LatLng(33.572206, -7.591855);
        mMap.addMarker(new MarkerOptions().position(Casablanca).title("Casablanca Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Casablanca, 2));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (gps.canGetLocation()) {
            // \n is for new line
            Toast.makeText(getApplicationContext(), "location enabled", Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng arg0) {
            updateMap();
        }
    });
}

    private void updateMap() {
        mMap.clear();
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(provider, 13000, 2, locationListener);
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(2));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").snippet("Estimated location!"));
        //camera settings
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 2);
        mMap.animateCamera(yourLocation);
        CameraUpdateFactory.scrollBy(-5, +5);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(2)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        new Thread(new sendData()).start();
        new Thread(new receiveData()).start();
        try {
        parseJSON.showData();
            try {
                latitude2 = Double.parseDouble(parseJSON.data[1].toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                longitude2 = Double.parseDouble(parseJSON.data[2].toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
        e.printStackTrace();
    }
       // Toast.makeText(this,
         //       parseJSON.data[1]+ parseJSON.data[2]+BackgroundWorker.MY_JSON, Toast.LENGTH_LONG).show();
        LatLng latLng2 = new LatLng(latitude2,longitude2);
        mMap.addMarker(new MarkerOptions().position(latLng2).title("He is here").snippet("Estimated location!"));
    }

   class sendData implements Runnable{

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            String type = "send";
            String lat_str = Double.toString(latitude);
            String lng_str = Double.toString(longitude);
            Looper.prepare();
            BackgroundWorker backgroundWorker = new BackgroundWorker(BeThere.this);
            backgroundWorker.execute(type, lat_str,lng_str);
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
    class receiveData implements Runnable{

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            String type = "receive";
            Looper.prepare();
            BackgroundWorker backgroundWorker = new BackgroundWorker(BeThere.this);
            backgroundWorker.execute(type, null,null);
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BeThere Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bethere.tarafi.bethere/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BeThere Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bethere.tarafi.bethere/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        mHandler.removeCallbacks(new sendData());
        client.disconnect();
    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
              new Thread(new sendData()).start();
                updateMap();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }
}
