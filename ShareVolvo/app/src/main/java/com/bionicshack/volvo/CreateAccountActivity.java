package com.bionicshack.volvo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAccountActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    String currentPhotoPath;

    // creates unique path for image
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // send picture grab intent
    public void capturePicture(View view) {

        final TextView debugText = findViewById(R.id.signup_error);
        debugText.setText("");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            debugText.setText("Error in creating the file");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.bionicshack.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    // takes image from gallery
    public Bitmap grabImage()  {

        final TextView debugText = findViewById(R.id.signup_error);

        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
        } catch (Exception e) {
            debugText.setText("Cannot capture image");
        }
        return null;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            final Context ctx = this.getApplicationContext();
            final TextView errorText = findViewById(R.id.signup_error);

            //Bundle extras = data.getExtras();
            // Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap imageBitmap = this.grabImage();
            if (imageBitmap == null)
                return;

            // send request to server
            String url ="http://" + getString(R.string.server_ip) +":5000/license_validation";
            RequestQueue queue = Volley.newRequestQueue(this);

            // image to send
            String jimage = encodeToBase64(imageBitmap, Bitmap.CompressFormat.PNG, 100);
            JSONObject req = new JSONObject();
            try {
                req.accumulate("image", jimage);
            } catch (JSONException e) {
                errorText.setText("json error");
                return;
            }

            // request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, req,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    SharedPreferences sharedPref = ctx.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean(getString(R.string.settings_auth), true);
                                    editor.putString(getString(R.string.settings_name), response.getString("name"));
                                    editor.putString(getString(R.string.settings_surname), response.getString("surname"));
                                    editor.commit();
                                    Intent intent = new Intent(ctx, CarMapActivity.class);
                                    startActivity(intent);
                                } else {
                                    errorText.setText(response.getString("message"));
                                }
                            } catch (JSONException e) {
                                errorText.setText("Error reading server response");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorText.setText(error.toString());
                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the RequestQueue.
            queue.add(request);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
