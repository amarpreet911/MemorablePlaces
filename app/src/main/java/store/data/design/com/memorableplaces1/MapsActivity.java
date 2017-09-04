package store.data.design.com.memorableplaces1;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleMap.OnMapLongClickListener {


    LocationManager locManager;
    String provider;
    Double lat, lon;
    Location location;
    boolean isGPSEnabled, isNetworkEnabled;
    String serviceProviderIs = null;
    @Override
    public void onLocationChanged(Location userLocation) {
        /*mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(userLocation.getLatitude(), userLocation.getLongitude(), 10)));*/
System.out.println("unintentionally reached onLocationChanged");

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

    private GoogleMap mMap;

    int locationSaved = -1;
    @Override
    public void onMapLongClick(LatLng point){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
      //if it fails then it must show date thts y
        String label = new Date().toString();
        try {
          //1: because we want single result
          List<Address> listAddresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
        if(listAddresses != null && listAddresses.size()>0){
            label = listAddresses.get(0).getAddressLine(0);
        }
            MainActivity.aryListPlaces.add(label);
            //notifies the arraY ADAPTER TO UPDATE COZ LIST HAS TO BE UPDATED
            MainActivity.aryAdap.notifyDataSetChanged();
          //adding latitude and longitude to locSave list which has positions as locationSaved
            MainActivity.locSave.add(point);

      }catch(Exception e){
          System.out.println("exc is " + e);
      }
        mMap.addMarker(new MarkerOptions().position(point).title(label).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locManager.getBestProvider(new Criteria(), false);
        setContentView(R.layout.activity_maps);
        Intent i = getIntent();



        //-1 is the default va;lue we need to set
        /*Log.i("Locatino Info, ", Integer.toString(i.getIntExtra("locationInfo", -1)));*/
        //this is basically the list position from previous activity
        locationSaved = i.getIntExtra("locationinfo: ", -1);
        System.out.println("locationsaved in oncreate is "+locationSaved);
     //   Log.i("locationinfo, ", Integer.toString(i.getIntExtra("locationInfo", -1)));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

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
        System.out.println("enters on map ready, teh val of Locationsaved is  "+locationSaved);
        //*-1 : doesnot exist, 0 means you are adding a location
    if(locationSaved != -1 && locationSaved !=0){
        locManager.removeUpdates(this);
        System.out.println("enters the looping loop");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.locSave.get(locationSaved), 10));

        mMap.addMarker(new MarkerOptions().position(MainActivity.locSave.get(locationSaved)).
                title(MainActivity.aryListPlaces.get(locationSaved)));
     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(MainActivity.locSave.get(locationSaved)));
     }
    //to check if the user is entering a new location
    else if (locationSaved == 0){
        availableNtw();
        checkStatus();
        System.out.println("entered the locationSaved changed::::::::::::::::::::;;");
        lat = location.getLatitude();
        lon = location.getLongitude();

        Log.i("Latitude is ", lat.toString());
        Log.i("Longitude is ", lon.toString());
     //   mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Your Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 12));
    }
    else{
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.
                PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.
                ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
System.out.println("enters the odd else");
            locManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }
        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
      /*  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap.setOnMapLongClickListener(this);
    }
    //could have been done through intent too this function relates to the line added in the AndroidMafist.xml
    //under MapsActivity
@Override
    public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()) {
            case android.R.id.home:
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.
                        PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.
                        ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locManager.removeUpdates(this);
                }
                this.finish();//by default goes to parent activity after finishing this activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void availableNtw() {
        System.out.println("available network reached");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.
                PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.
                ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isGPSEnabled = locManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if ((isNetworkEnabled != false) || (isGPSEnabled != false)) {
                if (isNetworkEnabled) {
                    location = locManager
                            .getLastKnownLocation(locManager.NETWORK_PROVIDER);
                    serviceProviderIs = "n/w";
                } else if (isGPSEnabled) {
                    location = locManager
                            .getLastKnownLocation(locManager.GPS_PROVIDER);
                    serviceProviderIs = "gps";
                } else {
                    System.out.println("No network support from gps or network");
                }
            }
        }
    }
    public void  checkStatus() {
System.out.println("check status reached");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.
                PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.
                ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                System.out.println("sorry no support of network or GPS");
            } else {
                if (isNetworkEnabled) {
                    locManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            10,
                            10, this);
                    Log.d("Network", "Network Enabled");
                    if (locManager != null) {
                        location = locManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                               /* latitude = location.getLatitude();
                                longitude = location.getLongitude();*/
                            System.out.println("Location is achieved in network");
                            Log.i("Location info", "location achieved(Network)");
                        } else {
                            System.out.println("Location can't be achieved n/w");
                            Log.i("Location info", "sorry no location found n/w");
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                10,
                                10, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locManager != null) {
                            location = locManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                  /*  latitude = location.getLatitude();
                                    longitude = location.getLongitude();*/
                                System.out.println("Location is achieved with gps");
                                Log.i("Location info", "location achieved with gps");
                            } else {
                                System.out.println("Location can't be achieved gps");
                                Log.i("Location info", "sorry no location found gps");
                            }
                        }
                    }
                }
            }
        }
    }
}

