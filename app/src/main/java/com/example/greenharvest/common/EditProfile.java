package com.example.greenharvest.common;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.logins.Location;
import com.example.greenharvest.model.Seller;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class EditProfile extends AppCompatActivity {

    private EditText sellerNameEditText;
    private AutoCompleteTextView stateAutoCompleteTextView;
    private AutoCompleteTextView cityAutoCompleteTextView;
    private EditText phoneNumberEditText;
    private Button updateButton;

    private DatabaseReference sellerRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize database references
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        sellerNameEditText = findViewById(R.id.edit_name);
        stateAutoCompleteTextView = findViewById(R.id.edit_state);
        cityAutoCompleteTextView = findViewById(R.id.edit_city);
        phoneNumberEditText = findViewById(R.id.edit_phone);
        updateButton = findViewById(R.id.button_update);

        // Set event listener for state selection
        stateAutoCompleteTextView.setOnItemClickListener((adapterView, view, position, l) -> {
            int selectedStateIndex = position;
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_dropdown_item_1line, Location.cities[selectedStateIndex]);
            cityAutoCompleteTextView.setAdapter(cityAdapter);
        });

        // Fetch and populate existing seller details
        if (currentUser != null) {
            String sellerId = currentUser.getUid();

            sellerRef.child(sellerId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get the seller object
                        Seller seller = dataSnapshot.getValue(Seller.class);
                        if (seller != null) {
                            sellerNameEditText.setText(seller.getSellerName());

                            // Set adapter for state dropdown menu
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_dropdown_item_1line, Location.state);
                            stateAutoCompleteTextView.setAdapter(stateAdapter);

                            stateAutoCompleteTextView.setText(seller.getState(), false);
                            stateAutoCompleteTextView.performCompletion();

                            // Set adapter for city dropdown menu based on the selected state
                            int selectedStateIndex = Arrays.asList(Location.state).indexOf(seller.getState());
                            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_dropdown_item_1line, Location.cities[selectedStateIndex]);
                            cityAutoCompleteTextView.setAdapter(cityAdapter);

                            cityAutoCompleteTextView.setText(seller.getCity());

                            phoneNumberEditText.setText(seller.getPhoneNumber());
                        }
                    }
                }
            });
        }

        updateButton.setOnClickListener(v -> updateSellerDetails());
    }

    private void updateSellerDetails() {
        String sellerName = sellerNameEditText.getText().toString().trim();
        String state = stateAutoCompleteTextView.getText().toString().trim();
        String city = cityAutoCompleteTextView.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        // Validate the input fields
        if (sellerName.isEmpty() || state.isEmpty() || city.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update seller details in the database
        if (currentUser != null) {
            String sellerId = currentUser.getUid();

            DatabaseReference sellerDataRef = sellerRef.child(sellerId);
            sellerDataRef.child("sellerName").setValue(sellerName);
            sellerDataRef.child("state").setValue(state);
            sellerDataRef.child("city").setValue(city);
            sellerDataRef.child("phoneNumber").setValue(phoneNumber)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfile.this, "Seller details updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Failed to update seller details", Toast.LENGTH_SHORT).show());
        }
    }
}
