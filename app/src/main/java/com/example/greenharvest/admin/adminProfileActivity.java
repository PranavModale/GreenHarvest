package com.example.greenharvest.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Admin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminProfileActivity extends AppCompatActivity {

    private TextView adminNameTextView;
    private TextView emailTextView;
    private TextView stateTextView;
    private TextView phoneNumberTextView;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Retrieve admin ID from Intent extras
        String adminId = getIntent().getStringExtra("adminId");

        // Initialize TextViews
        adminNameTextView = findViewById(R.id.adminName);
        emailTextView = findViewById(R.id.city);
        stateTextView = findViewById(R.id.state);
        phoneNumberTextView = findViewById(R.id.phoneNumber);

        // Retrieve and display admin's data
        displayAdminData(adminId);
    }

    // Retrieve and display admin's data
    private void displayAdminData(String adminId) {
        DatabaseReference adminRef = mDatabase.child("Admins").child(adminId);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Admin admin = snapshot.getValue(Admin.class);
                    if (admin != null) {
                        adminNameTextView.setText(admin.getAdminName());
                        emailTextView.setText(admin.getCity());
                        stateTextView.setText(admin.getState());
                        phoneNumberTextView.setText(admin.getPhoneNumber());
                    }
                } else {
                    Toast.makeText(adminProfileActivity.this, "Admin data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(adminProfileActivity.this, "Failed to retrieve admin data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
