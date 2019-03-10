package com.bionicshack.volvo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static java.lang.Math.round;

public class SharingDoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_done);

        Intent intent = getIntent();
        int carid = intent.getIntExtra(CarMapActivity.SELECTED_CAR_MODEL, 0);

        // send request to server
        String url ="http://" + getString(R.string.server_ip) +":5000/car";
        RequestQueue queue = Volley.newRequestQueue(this);

        // image to send
        JSONObject req = new JSONObject();
        try {
            req.accumulate("carid", carid);
            req.accumulate("action", "lock");
        } catch (JSONException e) {

        }

        final TextView consumed_txt = findViewById(R.id.consumed_fuel);
        final TextView covered_txt = findViewById(R.id.covered_km);
        final TextView total_cost = findViewById(R.id.total_cost);
        final TextView time_bonus = findViewById(R.id.time_bonus);
        final ImageView eco_image = findViewById(R.id.eco_result);

        // request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, req,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            DecimalFormat format = new DecimalFormat("0.00");
                            double cons = response.getDouble("cons");
                            double km = response.getDouble("km");
                            double btime = round(0.1*km/(100.0 / 3600.0) / 60.0);
                            consumed_txt.setText("Consumed fuel: " + format.format(cons) + "L");
                            covered_txt.setText("Covered km: " + format.format(km));
                            total_cost.setText("Total cost: " + format.format(response.getDouble("cost")) + "CHF");
                            if (cons/km < 3.0/100.0) {
                                time_bonus.setText(format.format(btime) + " min");
                                eco_image.setVisibility(View.VISIBLE);
                            } else {
                                time_bonus.setText(format.format(0.0) + " min");
                                eco_image.setVisibility(View.INVISIBLE);
                            }

                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        consumed_txt.setText(error.toString());
                    }
                });

        //request.setRetryPolicy(new DefaultRetryPolicy(
        //        3000,
        //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        //        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(request);
    }
}
