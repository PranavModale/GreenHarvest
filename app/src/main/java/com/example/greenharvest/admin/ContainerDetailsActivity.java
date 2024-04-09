package com.example.greenharvest.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContainerDetailsActivity extends AppCompatActivity {

    private TextView sellerNameTextView, phoneNumberTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_details);

        sellerNameTextView = findViewById(R.id.sellerName);
        phoneNumberTextView = findViewById(R.id.textSellerNumber);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        displaySellerData();
    }

    private void displaySellerData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String sellerId = currentUser.getUid();
            DatabaseReference sellerRef = mDatabase.child("Sellers").child(sellerId);
            sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Seller seller = snapshot.getValue(Seller.class);
                        if (seller != null) {
                            sellerNameTextView.setText("Seller Name: " + seller.getSellerName());
                            phoneNumberTextView.setText("Phone Number: " + seller.getPhoneNumber());
                        }
                    } else {
                        Toast.makeText(ContainerDetailsActivity.this, "Seller data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ContainerDetailsActivity.this, "Failed to retrieve seller data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ContainerDetailsActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
