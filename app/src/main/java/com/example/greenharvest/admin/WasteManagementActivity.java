package com.example.greenharvest.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.model.AvailableWaste;
import com.example.greenharvest.model.Container;
import com.example.greenharvest.seller.SellerProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WasteManagementActivity extends AppCompatActivity {

    // Define a variable to hold the waste ID
    private String wasteId;

    // Define a variable to hold the seller ID
    private String sellerId;


    // Declare TextViews to display waste information
    private TextView textWasteCategory;
    private TextView textAvailableWeight;

    // Declare Spinner for container selection
    private Spinner containerSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waste_management);

        // Get the waste ID from the Intent extras
        wasteId = getIntent().getStringExtra("wasteId");

        // Find the visitProfileButton by its ID
        Button visitProfileButton = findViewById(R.id.visitProfileButton);

        // Initialize TextViews
        textWasteCategory = findViewById(R.id.textWasteCategoryValue);
        textAvailableWeight = findViewById(R.id.textAvailableWeightValue);

        // Initialize Spinner
        containerSpinner = findViewById(R.id.containerSpinner);

        // Retrieve waste information from the database
        retrieveWasteInfo();

        // Get the seller ID from the Intent extras
        sellerId = getIntent().getStringExtra("sellerId");

        // Set OnClickListener to the visitProfileButton
        visitProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to SellerProfileActivity and pass the seller ID
                Intent intent = new Intent(WasteManagementActivity.this, SellerProfileActivity.class);
                intent.putExtra("sellerId", sellerId); // Assuming wasteId is the seller ID
                startActivity(intent);
            }
        });

        // Retrieve container numbers from the database
        String currentAdminId = getCurrentAdminId();
        retrieveContainerNumbers(currentAdminId);
    }

    // Method to retrieve waste information from the database
    private void retrieveWasteInfo() {
        DatabaseReference wasteRef = FirebaseDatabase.getInstance().getReference().child("AvailableWaste").child(wasteId);
        wasteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AvailableWaste availableWaste = dataSnapshot.getValue(AvailableWaste.class);
                    if (availableWaste != null) {
                        // Set the retrieved waste information to the TextViews
                        textWasteCategory.setText(availableWaste.getCategory());
                        textAvailableWeight.setText(String.valueOf(availableWaste.getWeight()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Method to retrieve container numbers from the database
    private void retrieveContainerNumbers(String adminId) {
        DatabaseReference containerRef = FirebaseDatabase.getInstance().getReference().child("Containers");
        containerRef.orderByChild("adminId").equalTo(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> containerNumbers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Container container = snapshot.getValue(Container.class);
                    if (container != null) {
                        containerNumbers.add(container.getContainerNumber());
                    }
                }
                // Populate the Spinner with container numbers
                ArrayAdapter<String> adapter = new ArrayAdapter<>(WasteManagementActivity.this, android.R.layout.simple_spinner_item, containerNumbers);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                containerSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Method to get the current admin ID (you need to implement this method based on your app's logic)
    private String getCurrentAdminId() {
        // Example method - replace this with your actual implementation
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // Handle case where admin is not logged in
        }
    }
}
