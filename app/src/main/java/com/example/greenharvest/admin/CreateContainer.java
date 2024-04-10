package com.example.greenharvest.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Admin;
import com.example.greenharvest.model.Container;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class CreateContainer extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextInputEditText containerNumberEditText, containerNameEditText, daysToConvertEditText, capacityEditText;
    private Button createContainerButton;
    private TextInputLayout categoryInputLayout;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_container);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Containers");

        // Initialize views
        containerNumberEditText = findViewById(R.id.containerNumberEditText);
        containerNameEditText = findViewById(R.id.containerNameEditText);
        daysToConvertEditText = findViewById(R.id.daysToConvert);
        capacityEditText = findViewById(R.id.capacityEditText);
        createContainerButton = findViewById(R.id.createContainerButton);
        categoryInputLayout = findViewById(R.id.categoryInputLayout);

        // Initialize dropdown adapter for categories
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.category_array));
        ((AutoCompleteTextView) categoryInputLayout.getEditText()).setAdapter(categoryAdapter);

        createContainerButton.setOnClickListener(v -> {
            // Create a new container object with input values
            Container container = new Container();
            container.setContainerNumber(containerNumberEditText.getText().toString().trim());
            container.setContainerName(containerNameEditText.getText().toString().trim());
            container.setCategory(categoryInputLayout.getEditText().getText().toString().trim());
            container.setCreationDate(new Date());
            container.setDaysToConvert(Integer.parseInt(daysToConvertEditText.getText().toString().trim()));
            container.setCapacity(Double.parseDouble(capacityEditText.getText().toString().trim()));

            // Set admin ID from current logged-in admin
            container.setAdminId(getCurrentAdminId());

            // Check if container number is unique and add container to database
            checkUniqueContainerNumber(container);
        });
    }

    // Method to get current admin ID
    private String getCurrentAdminId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case when there is no logged-in admin
            return "";
        }
    }

    // Method to check if container number is unique
    // Method to check if container number is unique for the current admin
    // Method to check if container number is unique for the current admin
    private void checkUniqueContainerNumber(final Container container) {
        databaseReference.orderByChild("containerNumber")
                .equalTo(container.getContainerNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isUnique = true;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Container existingContainer = snapshot.getValue(Container.class);
                            if (existingContainer != null && existingContainer.getAdminId().equals(getCurrentAdminId())) {
                                // Container number already exists for the current admin
                                isUnique = false;
                                break;
                            }
                        }
                        if (isUnique) {
                            // Container number is unique for the current admin, proceed to add to database
                            container.setArrayOfSellers(new ArrayList<>()); // Set empty list for now
                            String containerId = databaseReference.push().getKey();
                            container.setContainerId(containerId); // Set the containerId
                            databaseReference.child(containerId).setValue(container);
                            showSuccessDialog();
                        } else {
                            // Container number already exists for the current admin, display error message
                            Toast.makeText(CreateContainer.this, "Container number already exists for you. Please enter a different number.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Toast.makeText(CreateContainer.this, "Database error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Method to show success dialog and clear fields
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Container created successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear all fields
                    containerNumberEditText.setText("");
                    containerNameEditText.setText("");
                    categoryInputLayout.getEditText().setText("");
                    daysToConvertEditText.setText("");
                    capacityEditText.setText("");

                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }
}
