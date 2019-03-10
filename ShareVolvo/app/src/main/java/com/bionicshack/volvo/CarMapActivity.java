package com.bionicshack.volvo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker selectedMarker = null;
    public static CarsDatabase cars = null;

    public static final String SELECTED_CAR_MODEL = "com.bionicshack.volvo.SELECTED_CAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cars = new CarsDatabase();

        setContentView(R.layout.activity_car_map);
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

        // Add a marker on the steet close to the university and move the camera
        LatLng uni = new LatLng(47.4319, 9.375);
        //LatLng car1 = new LatLng(47.43225, 9.37429);
        //LatLng car2 = new LatLng(47.431706, 9.373588);
        //mMap.addMarker(new MarkerOptions().position(car1).title("V60 Twin Engine").snippet("Fuel: 70%, Cost: 0.22 CHF/min"));
        //mMap.addMarker(new MarkerOptions().position(car2).title("V60").snippet("Fuel: 50%, Cost: 0.20 CHF/min"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uni, 16.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                   marker.showInfoWindow();
                }
                selectedMarker = marker;
                return true;
            }
        });

        // set welcome message
        TextView welcome = findViewById(R.id.select_welcome);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String name = sharedPref.getString(getString(R.string.settings_name), "Error");
        String surname = sharedPref.getString(getString(R.string.settings_surname), "Error");
        welcome.setText("Welcome, " + name + " " + surname);

        // send request
        String url ="http://" + getString(R.string.server_ip) +":5000/get_cars";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONArray req = new JSONArray();

        final CarsDatabase carlist = cars;
        final GoogleMap map = mMap;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, req,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        carlist.setCars(response);
                        for (int i = 0; i < carlist.getNumCars(); i++) {
                            Car c = carlist.getCar(i);
                            LatLng ll = new LatLng(c.getLat(), c.getLon());
                            Marker m = map.addMarker(new MarkerOptions().position(ll).title(c.getModel()).snippet("Fuel " + c.getFuel() + "%, Cost: " + c.getCost() + "CHF/min"));
                            m.setTag(i);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LatLng ll = new LatLng(47.43225, 9.37429);
                        map.addMarker(new MarkerOptions().position(ll).title(error.toString()));
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    public void chooseCar(View view) {

        if (selectedMarker != null) {
            int selectedCar = (int) selectedMarker.getTag();
            Intent intent = new Intent(getApplicationContext(), CarUseActivity.class);
            intent.putExtra(SELECTED_CAR_MODEL, selectedCar);
            startActivity(intent);
        }

    }

}
