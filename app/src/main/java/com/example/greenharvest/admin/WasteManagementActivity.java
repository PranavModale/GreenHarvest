    package com.example.greenharvest.admin;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

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
        private TextView textContainerCapacity;
        private TextView textAvailableCapacity; // New TextView for available capacity

        // Declare Spinner for container selection
        private Spinner containerSpinner;

        // Declare EditText for weight input
        private EditText weightInput;

        // Declare Button to add weight
        private Button addWeightButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_waste_management);

            getSupportActionBar().hide();

            // Get the waste ID from the Intent extras
            wasteId = getIntent().getStringExtra("wasteId");

            // Find the visitProfileButton by its ID
            Button visitProfileButton = findViewById(R.id.visitProfileButton);

            // Initialize TextViews
            textWasteCategory = findViewById(R.id.textWasteCategoryValue);
            textAvailableWeight = findViewById(R.id.textAvailableWeightValue);
            textContainerCapacity = findViewById(R.id.textContainerCapacityValue);
            textAvailableCapacity = findViewById(R.id.textAvailableCapacityValue); // Initialize new TextView

            // Initialize Spinner
            containerSpinner = findViewById(R.id.containerSpinner);
            containerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedContainerNumber = (String) parent.getItemAtPosition(position);
                    retrieveContainerCapacity(selectedContainerNumber);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle case where nothing is selected
                }
            });

            // Initialize EditText and Button
            weightInput = findViewById(R.id.weightInput);
            addWeightButton = findViewById(R.id.addWeightButton);
            addWeightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWeightToContainer();
                }
            });

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

        // Method to retrieve and display container capacity based on selected container number
        private void retrieveContainerCapacity(String containerNumber) {
            DatabaseReference containerRef = FirebaseDatabase.getInstance().getReference().child("Containers");
            containerRef.orderByChild("containerNumber").equalTo(containerNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Container container = snapshot.getValue(Container.class);
                        if (container != null) {
                            String capacityText = "" + container.getCapacity();
                            textContainerCapacity.setText(capacityText);

                            // Calculate and display available capacity
                            double availableCapacity = container.getCapacity() - container.getTotalWeight();
                            textAvailableCapacity.setText(String.valueOf(availableCapacity));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        // Method to add weight to the selected container
        // Method to add weight to the selected container
        // Method to add weight to the selected container
        // Method to add weight to the selected container
    // Update the call to refreshPage in addWeightToContainer to pass the selected container number
    // Method to add weight to the selected container
        private void addWeightToContainer() {

            // Get the weight input from the EditText
            String weightInputText = weightInput.getText().toString();

            // Check if the weight input is empty
            // so that our app will not crash if we click button while edit text is empty
            if (TextUtils.isEmpty(weightInputText)) {
                // Display a toast message prompting the user to add weight first
                Toast.makeText(WasteManagementActivity.this, "Please enter a weight first", Toast.LENGTH_SHORT).show();
                return;
            }

            final String containerNumber = (String) containerSpinner.getSelectedItem();
            final double weightToAdd = Double.parseDouble(weightInputText);
            final String sellerId = getIntent().getStringExtra("sellerId");

            DatabaseReference containerRef = FirebaseDatabase.getInstance().getReference().child("Containers");
            containerRef.orderByChild("containerNumber").equalTo(containerNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Container container = snapshot.getValue(Container.class);
                        if (container != null) {
                            double currentTotalWeight = container.getTotalWeight();
                            double newTotalWeight = currentTotalWeight + weightToAdd;

                            // Retrieve the selected container's capacity from the database
                            double containerCapacity = container.getCapacity();

                            // Check if adding weight exceeds container capacity
                            if (newTotalWeight > containerCapacity) {
                                // Show toast indicating exceeding capacity
                                Toast.makeText(WasteManagementActivity.this, "Weight cannot be added. Exceeds container capacity.", Toast.LENGTH_SHORT).show();
                                return; // Exit onDataChange method
                            }

                            // Check if the weight to be added exceeds available waste weight
                            DatabaseReference availableWasteRef = FirebaseDatabase.getInstance().getReference().child("AvailableWaste").child(wasteId);
                            availableWasteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        AvailableWaste availableWaste = dataSnapshot.getValue(AvailableWaste.class);
                                        if (availableWaste != null) {
                                            double currentAvailableWeight = availableWaste.getWeight();
                                            if (weightToAdd > currentAvailableWeight) {
                                                // Show toast indicating insufficient available weight
                                                Toast.makeText(WasteManagementActivity.this, "Weight cannot be added. Insufficient available weight.", Toast.LENGTH_SHORT).show();
                                                return; // Exit onDataChange method
                                            }
                                            // If weight to be added is within available weight, proceed to add it to container
                                            snapshot.getRef().child("totalWeight").setValue(newTotalWeight);
                                            // Update available waste weight
                                            double newAvailableWeight = currentAvailableWeight - weightToAdd;
                                            dataSnapshot.getRef().child("weight").setValue(newAvailableWeight);
                                            // Check if available waste weight becomes zero and delete node if so
                                            if (newAvailableWeight <= 0) {
                                                dataSnapshot.getRef().removeValue();
                                            }

                                            // Add seller ID to the container's list of sellers
                                            ArrayList<String> sellers = container.getArrayOfSellers();
                                            if (sellers == null || !sellers.contains(sellerId)) {
                                                if (sellers == null) {
                                                    sellers = new ArrayList<>();
                                                }
                                                sellers.add(sellerId);
                                                snapshot.getRef().child("arrayOfSellers").setValue(sellers);
                                            }

                                            // Refresh the page after adding weight
                                            refreshPage(containerSpinner.getSelectedItemPosition());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });

            // Clear weight input after adding weight
            weightInput.setText("");
        }











        // Method to refresh the page
        // Method to refresh the page
        // Method to refresh the page
        // Method to refresh the available weight
        private void refreshAvailableWeight() {
            // Retrieve and update the available weight
            retrieveWasteInfo();
        }

        // Method to refresh the page
        // Modify the refreshPage method to preserve the selected container
        // Method to refresh the page
// Modify the refreshPage method to preserve the selected container
        private void refreshPage(int selectedContainerPosition) {
            // Save the selected container's position
            final String selectedContainerNumber = (String) containerSpinner.getSelectedItem();

            Intent intent = getIntent();
            finish();
            startActivity(intent);

            // Restore the selected container's position
            containerSpinner.post(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) containerSpinner.getAdapter();
                    if (adapter != null) {
                        int position = adapter.getPosition(selectedContainerNumber);
                        if (position != -1) {
                            containerSpinner.setSelection(position);
                        }
                    }
                }
            });

            // Refresh the available weight as well
            refreshAvailableWeight();

            // Refresh the available capacity as well
            retrieveContainerCapacity(selectedContainerNumber);
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
