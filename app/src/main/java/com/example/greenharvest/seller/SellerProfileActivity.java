package com.example.greenharvest.seller;

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

public class SellerProfileActivity extends AppCompatActivity {

    private TextView sellerNameTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView phoneNumberTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_c__seller);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize TextViews
        sellerNameTextView = findViewById(R.id.sellerName);
        cityTextView = findViewById(R.id.city);
        stateTextView = findViewById(R.id.state);
        phoneNumberTextView = findViewById(R.id.phoneNumber);

        // Retrieve and display seller's data
        displaySellerData();
    }

    // Retrieve and display seller's data
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
                            sellerNameTextView.setText(seller.getSellerName());
                            cityTextView.setText(seller.getCity());
                            stateTextView.setText(seller.getState());
                            phoneNumberTextView.setText(seller.getPhoneNumber());
                        }
                    } else {
                        Toast.makeText(SellerProfileActivity.this, "Seller data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SellerProfileActivity.this, "Failed to retrieve seller data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SellerProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
