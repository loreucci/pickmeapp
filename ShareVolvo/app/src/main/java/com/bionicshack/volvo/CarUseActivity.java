package com.bionicshack.volvo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CarUseActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean locked = true;
    private int carid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_use);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        this.carid = intent.getIntExtra(CarMapActivity.SELECTED_CAR_MODEL, 0);
        Car car = CarMapActivity.cars.getCar(carid);

        // Capture the layout's TextView and set the string as its text
        TextView carview = findViewById(R.id.selected_car);
        TextView fuelview = findViewById(R.id.fuel_level);
        TextView costview = findViewById(R.id.price_min);
        carview.setText("Selected car: " + car.getModel());
        fuelview.setText("Fuel level: " + car.getFuel() + "%");
        costview.setText("Price: " + car.getCost() + "CHF/min");

        TextView greentext = findViewById(R.id.green_text);
        ImageView greenimage = findViewById(R.id.green_image);
        if (!car.isHybrid()) {
            greentext.setVisibility(View.INVISIBLE);
            greenimage.setVisibility(View.INVISIBLE);
        }

        Button button = findViewById(R.id.unlock_btn);
        TextView unlockTxt = findViewById(R.id.unlock_txt);
        button.setOnClickListener(this);
        if (locked) {
            button.setText("Unlock");
            unlockTxt.setText("Press button to unlock car");
        }

    }

    public void onClick(View v) {

        Button button = findViewById(R.id.unlock_btn);
        TextView unlockTxt = findViewById(R.id.unlock_txt);

        if (locked) {

            button.setText("Lock");
            unlockTxt.setText("Press button after parking to lock car again");

            locked = false;

            // send request to server
            String url ="http://" + getString(R.string.server_ip) +":5000/car";
            RequestQueue queue = Volley.newRequestQueue(this);

            // image to send
            JSONObject req = new JSONObject();
            try {
                req.accumulate("carid", carid);
                req.accumulate("action", "unlock");
            } catch (JSONException e) {

            }

            // request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, req,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //request.setRetryPolicy(new DefaultRetryPolicy(
            //        3000,
            //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            //        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the RequestQueue.
            queue.add(request);

        } else {

            locked = true;

            Intent intent = new Intent(getApplicationContext(), SharingDoneActivity.class);
            intent.putExtra(CarMapActivity.SELECTED_CAR_MODEL, this.carid);
            startActivity(intent);

        }

    }
}
