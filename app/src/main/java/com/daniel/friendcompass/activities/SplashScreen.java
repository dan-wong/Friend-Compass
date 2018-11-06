package com.daniel.friendcompass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.activities.AuthenticationActivities.SignInActivity;
import com.daniel.friendcompass.activities.MainActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser == null) {
                    Intent mainIntent = new Intent(SplashScreen.this, SignInActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                } else {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                }
            }
        }, 1000);
    }
}
