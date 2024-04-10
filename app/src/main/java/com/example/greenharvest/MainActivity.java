package com.example.greenharvest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.admin.AdminMain;
import com.example.greenharvest.buyer.BuyerMain;
import com.example.greenharvest.logins.LoginActivity;
import com.example.greenharvest.logins.SignUpActivity;
import com.example.greenharvest.seller.SellerMain;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); // Hide the action bar

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "");
            if (role.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AdminMain.class);
                startActivity(intent);
                finish();
            } else if (role.equals("seller")) {
                Intent intent = new Intent(MainActivity.this, SellerMain.class);
                startActivity(intent);
                finish();
            } else if (role.equals("buyer")) {
                Intent intent = new Intent(MainActivity.this, BuyerMain.class);
                startActivity(intent);
                finish();
            }
        }

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        button2.setOnClickListener(v -> {
            // Navigate to SignUpActivity
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
