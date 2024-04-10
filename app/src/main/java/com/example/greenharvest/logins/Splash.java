package com.example.greenharvest.logins;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;


public class Splash extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 100;
    private boolean splashFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!splashFinished) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //
                //window.setStatusBarColor(getResources().getColor(R.color.green)); // Replace with your desired color
            }

            setContentView(R.layout.activity_splash);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check if the splashFinished flag is still false before navigating to the welcome activity
                    if (!splashFinished) {
                        splashFinished = true;
                        // Intent is used to switch from one activity to another.
                        Intent i = new Intent(Splash.this, LoginActivity.class);
                        startActivity(i); // invoke the welcome activity.
                        finish(); // the current activity will get finished.
                    }
                }
            }, SPLASH_SCREEN_TIME_OUT);
        } else {
            // If the splash has already finished, simply navigate to the welcome activity
            Intent i = new Intent(Splash.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
