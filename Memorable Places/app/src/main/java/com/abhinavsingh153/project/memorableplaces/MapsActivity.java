package com.abhinavsingh153.project.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == 1){

             if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                 if (ContextCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0,0,locationListener);

                     Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                     centerMapOnLocation(lastKnownLocation , "Your Location");
                 }
             }
         }

    }


    public void centerMapOnLocation (Location location , String title){

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();

        if (title != "Your Location"){

            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

      // mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation , 10));

        Log.i("Location" , location.toString());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        if (intent.getIntExtra("PlaceNumber" , 0) == 0){

            // Zoomm in on that location

            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerMapOnLocation(location , "Your Location");

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (Build.VERSION.SDK_INT < 23){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0,0,locationListener);
            }

            else {

                  if (ContextCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){


                      ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , 1);
                  }

                  else{

                      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0,0,locationListener);

                      Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                      centerMapOnLocation(lastKnownLocation , "Your Location");

                  }

            }

        }


        else {
            // displaying the marker on the map to the corresponding clicked listView item

            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("PlaceNumber",0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("PlaceNumber",0)).longitude);

           centerMapOnLocation(placeLocation , MainActivity.placesList.get(intent.getIntExtra("PlaceNumber" , 0)));


        }

        //Toast.makeText(this,intent.getIntExtra("PlaceNumber" , 0), Toast.LENGTH_SHORT).show();

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(this , Locale.getDefault());

        String address = "";

        try {

            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude , latLng.longitude ,1);

            if (listAddresses != null && listAddresses.size() > 0){

                Log.i("Address" , listAddresses.get(0).toString());

                if (listAddresses.get(0).getThoroughfare() != null){

                    if(listAddresses.get(0).getSubThoroughfare() != null){

                        address += listAddresses.get(0).getSubThoroughfare();
                    }

                    address += listAddresses.get(0).getThoroughfare();


                }



            }


        } catch (IOException e) {


            e.printStackTrace();
        }

        if(address == ""){

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-mm-dd");
            address= sdf.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        // updating the lis View when a long pree is done

        MainActivity.locations.add(latLng);
        MainActivity.placesList.add(address);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.abhinavsingh153.project.memorableplaces",  Context.MODE_PRIVATE);

        try {

            ArrayList<String> latitude = new ArrayList<String>();
            ArrayList<String> longitude = new ArrayList<String>();

            for (LatLng coordinates: MainActivity.locations){

                latitude.add(Double.toString(coordinates.latitude));
                longitude.add(Double.toString(coordinates.longitude));
            }

            sharedPreferences.edit().putString("NewLatitude" , ObjectSerializer.serialize(latitude)).apply();

            sharedPreferences.edit().putString("NewLongitude" , ObjectSerializer.serialize(longitude)).apply();

            sharedPreferences.edit().putString("placesList" , ObjectSerializer.serialize(MainActivity.placesList)).apply();

        } catch (IOException e) {

            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext() , "Location saved." , Toast.LENGTH_SHORT).show();
    }
}
