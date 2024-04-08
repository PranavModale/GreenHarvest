package com.example.greenharvest.seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenharvest.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerMain extends AppCompatActivity {

    private BottomNavigationView bnView;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        bnView = findViewById(R.id.bottomNavigationView);
        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new A_Seller();
                } else if (id == R.id.nav_add) {
                    selectedFragment = new B_Seller();
                } else if (id == R.id.nav_profile) {
                    selectedFragment = new C_Seller();
                } 

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }

                return true;
            }
        });

        // Set the home fragment as the default fragment
        loadFragment(new A_Seller());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Clear the activity stack and go to the device's home screen
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
