package com.example.group25_inclass11;

/**
 * Assignment #: Group25_InClass11
 * File Name: Group25_InClass11 MainActivity.java
 * Full Name: Kristin Pflug
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Request mapRequest = new Request.Builder()
                .url("https://www.theappsdr.com/map/route")
                .build();

        client.newCall(mapRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject mapJSONObject = new JSONObject(response.body().string());
                        JSONArray pointsArray = mapJSONObject.getJSONArray("path");
                        ArrayList<LatLng> points = new ArrayList<>();

                        for(int i = 0; i < pointsArray.length(); i++){
                            JSONObject pointObject = pointsArray.getJSONObject(i);
                            double latitude = pointObject.getDouble("latitude");
                            double longitude = pointObject.getDouble("longitude");
                            LatLng point = new LatLng(latitude, longitude);

                            points.add(point);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                        .startCap(new RoundCap())
                                        .endCap(new RoundCap())
                                        .addAll(points));

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for(LatLng point : points) {
                                    builder.include(point);
                                }
                                LatLngBounds bounds = builder.build();

                                LatLng startPoint = points.get(0);
                                LatLng endPoint = points.get(points.size() -1);

                                googleMap.addMarker(new MarkerOptions()
                                        .position(startPoint)
                                        .title("Starting Point"));

                                googleMap.addMarker(new MarkerOptions()
                                        .position(endPoint)
                                        .title("Ending Point"));

                                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}