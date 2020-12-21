package com.example.pharmacy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    Button register, login, goThrough;
    private static final String TAG_ID = "id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goThrough = findViewById(R.id.guest);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        goThrough.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), WorkScreen.class);
            intent.putExtra(TAG_ID, "noId");
            startActivity(intent);
        });
        register.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
        });
        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });
    }
}