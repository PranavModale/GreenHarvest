package com.example.greenharvest.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenharvest.R;
import com.example.greenharvest.model.AvailableWaste;
import com.example.greenharvest.model.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class A_Admin extends Fragment {

    private LinearLayout linearLayout;
    private DatabaseReference availableWasteRef;
    private DatabaseReference sellerRef;
    private Button datePickerButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database references
        availableWasteRef = FirebaseDatabase.getInstance().getReference().child("AvailableWaste");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a__admin, container, false);

        linearLayout = view.findViewById(R.id.linear_layout);
        datePickerButton = view.findViewById(R.id.datePickerButton); // Initialize datePickerButton

        // Fetch available waste data from Firebase Database
        fetchAvailableWaste();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the click listener for the datePickerButton
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show DatePickerDialog to select date
                showDatePickerDialog();
            }
        });
    }

    private void fetchAvailableWaste() {
        // Query to fetch all available waste items
        Query query = availableWasteRef.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AvailableWaste availableWaste = snapshot.getValue(AvailableWaste.class);
                    if (availableWaste != null) {
                        // Create a new card view for each available waste item
                        fetchSellerInfo(availableWaste);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(getContext(), "Failed to fetch available waste: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSellerInfo(AvailableWaste availableWaste) {
        String sellerId = availableWaste.getSellerId();
        sellerRef.child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Seller seller = dataSnapshot.getValue(Seller.class);
                if (seller != null) {
                    // Create card view with available waste and seller information
                    View itemView = createWasteCardView(availableWaste, seller);
                    linearLayout.addView(itemView);

                    // Add OnClickListener to the cardview
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open WasteManagementActivity and pass sellerId and wasteId
                            Intent intent = new Intent(getActivity(), WasteManagementActivity.class);
                            intent.putExtra("sellerId", sellerId);
                            intent.putExtra("wasteId", availableWaste.getWasteId());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private View createWasteCardView(AvailableWaste availableWaste, Seller seller) {
        if (getContext() == null) {
            return null; // Return null if context is null
        }

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_waste_card, null);

        TextView textSellerName = itemView.findViewById(R.id.textSellerName);
        TextView textDate = itemView.findViewById(R.id.textDate);
        TextView textWasteType = itemView.findViewById(R.id.textWasteType);
        TextView textWeight = itemView.findViewById(R.id.textWeight);

        // Format the date as "dd-MM-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(availableWaste.getDate());

        // Set values from availableWaste and seller objects to the card view
        textSellerName.setText(seller.getSellerName());
        textDate.setText(formattedDate);
        textWasteType.setText(availableWaste.getCategory());
        textWeight.setText("Weight: " + availableWaste.getWeight());

        return itemView;
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String formattedDate = dateFormat.format(selectedDate.getTime());
            datePickerButton.setText(formattedDate);
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}