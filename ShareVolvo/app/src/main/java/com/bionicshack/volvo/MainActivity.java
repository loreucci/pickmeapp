package com.bionicshack.volvo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        SharedPreferences sharedPref = ctx.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean already_authorized = sharedPref.getBoolean(getString(R.string.settings_auth), false);

        final Button button = findViewById(R.id.join_btn);

        if (already_authorized) {
            button.setText("Book");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CarMapActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            button.setText("Join");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    public void goCreateAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }

    public void goDirectlyToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), CarMapActivity.class);
        startActivity(intent);
    }
}
