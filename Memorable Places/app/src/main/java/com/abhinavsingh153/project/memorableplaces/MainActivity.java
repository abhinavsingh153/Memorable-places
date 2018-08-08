package com.abhinavsingh153.project.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<LatLng> locations = new ArrayList<LatLng>();

    static ArrayList<String> placesList = new ArrayList<String>();

    static  ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);



        ArrayList<String> latitude = new ArrayList<String>();
        ArrayList<String> longitude = new ArrayList<String>();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.abhinavsingh153.project.memorableplaces", Context.MODE_PRIVATE);

        placesList.clear();
        latitude.clear();
        longitude.clear();
        locations.clear();

        try {
            latitude =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("NewLatitude" , ObjectSerializer.serialize(new ArrayList<String>())));

            longitude =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("NewLongitude" , ObjectSerializer.serialize(new ArrayList<String>())));

            placesList =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("placesList" , ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {

            e.printStackTrace();

        }

        if(placesList.size() > 0 && latitude.size() > 0 && longitude.size() > 0){

            if (placesList.size()== latitude.size() && latitude.size()== longitude.size()){

                for (int i = 0 ; i < latitude.size() ; i++){

                    locations.add(new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))));
                }
            }

            Log.i("If","If is running");

            Log.i("Location" , locations.toString());

        }

        else{
            locations.add(new LatLng(0,0));

            placesList.add("Add location...");

            Log.i("Else" , "Else is running");


        }


        arrayAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, placesList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class );

              //  Toast.makeText(MainActivity.this, "Hello! " + position, Toast.LENGTH_SHORT).show();

                intent.putExtra("PlaceNumber" , position);

                startActivity(intent);

            }
        });




    }
}
